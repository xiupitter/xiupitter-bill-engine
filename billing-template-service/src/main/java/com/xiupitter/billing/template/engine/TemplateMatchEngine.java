package com.xiupitter.billing.template.engine;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiupitter.billing.template.entity.BillingTemplate;
import com.xiupitter.billing.template.mapper.BillingTemplateMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 模板匹配引擎
 *
 * 核心功能：
 * 1. 根据业务类型查询所有可用模板
 * 2. 根据维度条件进行精确匹配或模糊匹配
 * 3. 根据优先级排序，选择最合适的模板
 * 4. 如果没有匹配到，返回默认模板
 *
 * @author xiupitter
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TemplateMatchEngine {

    private final BillingTemplateMapper templateMapper;

    /**
     * 匹配计费模板
     *
     * @param bizType    业务类型
     * @param dimensions 维度条件
     * @return 匹配的模板，如果没有匹配到则返回null
     */
    public BillingTemplate matchTemplate(String bizType, Map<String, Object> dimensions) {
        log.info("开始匹配计费模板, bizType={}, dimensions={}", bizType, dimensions);

        // 1. 查询该业务类型下所有启用的模板
        List<BillingTemplate> templates = queryAvailableTemplates(bizType);

        if (CollectionUtils.isEmpty(templates)) {
            log.warn("未找到可用的计费模板, bizType={}", bizType);
            return null;
        }

        // 2. 过滤出当前时间有效的模板
        LocalDateTime now = LocalDateTime.now();
        templates = templates.stream()
                .filter(template -> isEffective(template, now))
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(templates)) {
            log.warn("未找到有效期内的计费模板, bizType={}", bizType);
            return null;
        }

        // 3. 根据维度条件匹配模板
        List<TemplateMatchResult> matchResults = templates.stream()
                .map(template -> matchDimensions(template, dimensions))
                .filter(result -> result.getMatchScore() > 0) // 过滤掉不匹配的
                .sorted(Comparator
                        .comparing(TemplateMatchResult::getMatchScore).reversed() // 匹配度从高到低
                        .thenComparing(result -> result.getTemplate().getPriority(), Comparator.reverseOrder())) // 优先级从高到低
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(matchResults)) {
            // 4. 如果没有匹配到，尝试返回默认模板
            log.info("未找到匹配的模板，尝试使用默认模板");
            return templates.stream()
                    .filter(t -> t.getIsDefault() != null && t.getIsDefault() == 1)
                    .findFirst()
                    .orElse(null);
        }

        // 5. 返回最佳匹配的模板
        BillingTemplate bestMatch = matchResults.get(0).getTemplate();
        log.info("匹配到计费模板, templateCode={}, templateName={}, matchScore={}",
                bestMatch.getTemplateCode(), bestMatch.getTemplateName(),
                matchResults.get(0).getMatchScore());

        return bestMatch;
    }

    /**
     * 查询可用的模板
     */
    private List<BillingTemplate> queryAvailableTemplates(String bizType) {
        return templateMapper.selectList(new LambdaQueryWrapper<BillingTemplate>()
                .eq(BillingTemplate::getBizType, bizType)
                .eq(BillingTemplate::getStatus, 1) // 状态：启用
                .orderByDesc(BillingTemplate::getPriority)
                .orderByDesc(BillingTemplate::getCreateTime));
    }

    /**
     * 判断模板是否在有效期内
     */
    private boolean isEffective(BillingTemplate template, LocalDateTime now) {
        LocalDateTime startTime = template.getEffectiveStartTime();
        LocalDateTime endTime = template.getEffectiveEndTime();

        boolean afterStart = (startTime == null) || now.isAfter(startTime) || now.isEqual(startTime);
        boolean beforeEnd = (endTime == null) || now.isBefore(endTime);

        return afterStart && beforeEnd;
    }

    /**
     * 匹配维度条件
     *
     * @param template   模板
     * @param dimensions 维度条件
     * @return 匹配结果
     */
    private TemplateMatchResult matchDimensions(BillingTemplate template, Map<String, Object> dimensions) {
        TemplateMatchResult result = new TemplateMatchResult();
        result.setTemplate(template);

        // 如果模板没有配置维度条件，视为完全匹配（兜底模板）
        if (!StringUtils.hasText(template.getDimensionConfig())) {
            result.setMatchScore(1);
            return result;
        }

        // 如果请求没有传维度条件，但模板有配置，则不匹配
        if (CollectionUtils.isEmpty(dimensions)) {
            result.setMatchScore(0);
            return result;
        }

        try {
            JSONObject dimensionConfig = JSON.parseObject(template.getDimensionConfig());

            int totalConditions = dimensionConfig.size();
            int matchedConditions = 0;

            // 遍历模板配置的每个维度条件
            for (String dimensionKey : dimensionConfig.keySet()) {
                Object expectedValue = dimensionConfig.get(dimensionKey);
                Object actualValue = dimensions.get(dimensionKey);

                if (matchDimensionValue(expectedValue, actualValue)) {
                    matchedConditions++;
                }
            }

            // 计算匹配分数：匹配的条件数 / 总条件数
            // 必须全部匹配才算匹配成功
            if (matchedConditions == totalConditions) {
                result.setMatchScore(totalConditions);
            } else {
                result.setMatchScore(0);
            }

        } catch (Exception e) {
            log.error("维度匹配异常, templateCode={}", template.getTemplateCode(), e);
            result.setMatchScore(0);
        }

        return result;
    }

    /**
     * 匹配维度值
     *
     * @param expected 期望值（可以是单个值、数组、范围）
     * @param actual   实际值
     * @return 是否匹配
     */
    private boolean matchDimensionValue(Object expected, Object actual) {
        if (expected == null && actual == null) {
            return true;
        }

        if (expected == null || actual == null) {
            return false;
        }

        // 1. 精确匹配
        if (expected.equals(actual)) {
            return true;
        }

        // 2. 数组匹配（支持多值）
        if (expected instanceof List) {
            List<?> expectedList = (List<?>) expected;
            return expectedList.contains(actual);
        }

        // 3. 范围匹配（针对数值型维度）
        if (expected instanceof JSONObject) {
            JSONObject range = (JSONObject) expected;
            if (range.containsKey("min") || range.containsKey("max")) {
                try {
                    double actualNum = Double.parseDouble(actual.toString());
                    Double min = range.getDouble("min");
                    Double max = range.getDouble("max");

                    boolean afterMin = (min == null) || actualNum >= min;
                    boolean beforeMax = (max == null) || actualNum <= max;

                    return afterMin && beforeMax;
                } catch (Exception e) {
                    return false;
                }
            }
        }

        return false;
    }

    /**
     * 模板匹配结果
     */
    private static class TemplateMatchResult {
        private BillingTemplate template;
        private int matchScore; // 匹配分数，越高越匹配

        public BillingTemplate getTemplate() {
            return template;
        }

        public void setTemplate(BillingTemplate template) {
            this.template = template;
        }

        public int getMatchScore() {
            return matchScore;
        }

        public void setMatchScore(int matchScore) {
            this.matchScore = matchScore;
        }
    }
}
