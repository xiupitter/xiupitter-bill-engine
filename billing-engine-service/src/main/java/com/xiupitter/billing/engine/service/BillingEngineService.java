package com.xiupitter.billing.engine.service;

import com.alibaba.fastjson2.JSON;
import com.xiupitter.billing.api.dto.BillingRequest;
import com.xiupitter.billing.api.dto.BillingResult;
import com.xiupitter.billing.api.dto.BillingTemplateDTO;
import com.xiupitter.billing.engine.entity.BillingRecord;
import com.xiupitter.billing.engine.mapper.BillingRecordMapper;
import com.xiupitter.billing.formula.service.BillingFormulaService;
import com.xiupitter.billing.template.service.BillingTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mvel2.MVEL;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 计费引擎服务
 *
 * 核心业务逻辑：
 * 1. 模板匹配：根据业务类型和维度条件选择合适的计费模板
 * 2. 要素校验：校验必填要素和数据格式
 * 3. 计费执行：遍历模板中的所有计费项，逐项计算
 * 4. 结果汇总：汇总所有计费项的金额
 * 5. 记录保存：保存计费记录（非试算模式）
 *
 * @author xiupitter
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BillingEngineService {

    private final BillingRecordMapper billingRecordMapper;
    private final BillingTemplateService billingTemplateService;
    private final BillingFormulaService billingFormulaService;

    /**
     * 执行计费
     *
     * @param request 计费请求
     * @return 计费结果
     */
    @Transactional(rollbackFor = Exception.class)
    public BillingResult billing(BillingRequest request) {
        log.info("开始计费, bizNo={}, bizType={}, dryRun={}",
                 request.getBizNo(), request.getBizType(), request.getDryRun());

        try {
            // 1. 模板匹配
            BillingTemplateDTO template;
            if (StringUtils.hasText(request.getTemplateCode())) {
                // 指定了模板编码，直接查询
                template = billingTemplateService.getTemplateDetail(request.getTemplateCode());
            } else {
                // 未指定模板，根据维度匹配
                template = billingTemplateService.matchTemplate(
                        request.getBizType(), request.getDimensions());
            }

            if (template == null) {
                throw new RuntimeException("未找到匹配的计费模板");
            }

            log.info("匹配到计费模板: templateCode={}, templateName={}",
                    template.getTemplateCode(), template.getTemplateName());

            // 2. 要素校验
            validateFactors(request.getFactors());

            // 3. 执行计费
            List<BillingResult.BillingItemResult> itemResults = new ArrayList<>();
            BigDecimal totalFee = BigDecimal.ZERO;

            if (!CollectionUtils.isEmpty(template.getItems())) {
                for (BillingTemplateDTO.BillingItemDTO item : template.getItems()) {
                    // 判断是否需要计算该项
                    if (!shouldCalculate(item.getCalculateCondition(), request.getFactors())) {
                        log.debug("跳过计费项: itemCode={}, 不满足计算条件", item.getItemCode());
                        continue;
                    }

                    // 直接调用公式服务执行计算
                    try {
                        BigDecimal amount = billingFormulaService.calculate(
                                item.getFormulaId(), request.getFactors());

                        // 构建计费项结果
                        BillingResult.BillingItemResult itemResult = new BillingResult.BillingItemResult();
                        itemResult.setItemCode(item.getItemCode());
                        itemResult.setItemName(item.getItemName());
                        itemResult.setItemType(item.getItemType());
                        itemResult.setAmount(amount);
                        itemResult.setInputParams(request.getFactors());

                        itemResults.add(itemResult);

                        // 累加总费用
                        totalFee = totalFee.add(amount);

                        log.info("计费项计算完成: itemCode={}, amount={}", item.getItemCode(), amount);

                    } catch (Exception e) {
                        log.error("计费项计算失败: itemCode={}", item.getItemCode(), e);
                        if (item.getRequired() == 1) {
                            throw new RuntimeException("必需计费项计算失败: " + item.getItemName(), e);
                        }
                    }
                }
            }

            // 4. 构建结果
            BillingResult result = new BillingResult();
            result.setBizNo(request.getBizNo());
            result.setBizType(request.getBizType());
            result.setTemplateCode(template.getTemplateCode());
            result.setTemplateName(template.getTemplateName());
            result.setItems(itemResults);
            result.setTotalFee(totalFee);
            result.setBillingTime(System.currentTimeMillis());
            result.setDryRun(request.getDryRun());

            // 5. 保存计费记录（非试算）
            if (!Boolean.TRUE.equals(request.getDryRun())) {
                Long recordId = saveBillingRecord(request, result);
                result.setBillingRecordId(recordId);
            }

            log.info("计费完成, bizNo={}, totalFee={}, itemCount={}, dryRun={}",
                     request.getBizNo(), totalFee, itemResults.size(), request.getDryRun());

            return result;

        } catch (Exception e) {
            log.error("计费失败, bizNo={}", request.getBizNo(), e);
            throw new RuntimeException("计费失败: " + e.getMessage(), e);
        }
    }

    /**
     * 校验计费要素
     */
    private void validateFactors(Map<String, Object> factors) {
        if (CollectionUtils.isEmpty(factors)) {
            throw new RuntimeException("计费要素不能为空");
        }
        // TODO: 可以根据要素定义进行更详细的校验
        // 1. 检查必填要素是否都有值
        // 2. 检查数据类型是否正确
        // 3. 检查数据格式是否符合验证规则
    }

    /**
     * 判断是否需要计算该项
     */
    private boolean shouldCalculate(String calculateCondition, Map<String, Object> factors) {
        if (!StringUtils.hasText(calculateCondition)) {
            return true;
        }

        try {
            Object result = MVEL.eval(calculateCondition, factors);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.warn("计算条件表达式执行失败, condition={}", calculateCondition, e);
            return false;
        }
    }

    /**
     * 保存计费记录
     */
    private Long saveBillingRecord(BillingRequest request, BillingResult result) {
        BillingRecord record = new BillingRecord();
        record.setBizNo(request.getBizNo());
        record.setBizType(request.getBizType());
        record.setTemplateCode(result.getTemplateCode());
        record.setFactors(JSON.toJSONString(request.getFactors()));
        record.setDimensions(JSON.toJSONString(request.getDimensions()));
        record.setTotalFee(result.getTotalFee());
        record.setItemDetails(JSON.toJSONString(result.getItems()));
        record.setBillingTime(LocalDateTime.now());
        record.setStatus(1); // 1:计费成功

        billingRecordMapper.insert(record);
        return record.getId();
    }

    /**
     * 查询计费记录
     */
    public BillingRecord getByBizNo(String bizNo) {
        return billingRecordMapper.selectByBizNo(bizNo);
    }
}
