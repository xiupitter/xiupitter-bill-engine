package com.xiupitter.billing.formula.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiupitter.billing.formula.engine.FormulaCalculator;
import com.xiupitter.billing.formula.entity.BillingFormula;
import com.xiupitter.billing.formula.mapper.BillingFormulaMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 计费公式服务
 *
 * @author xiupitter
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BillingFormulaService extends ServiceImpl<BillingFormulaMapper, BillingFormula> {

    private final FormulaCalculator formulaCalculator;

    /**
     * 根据公式ID查询（带缓存）
     */
    @Cacheable(value = "billing:formula", key = "#formulaId")
    public BillingFormula getFormulaById(Long formulaId) {
        return this.getById(formulaId);
    }

    /**
     * 执行公式计算
     *
     * @param formulaId 公式ID
     * @param context   计算上下文
     * @return 计算结果
     */
    public BigDecimal calculate(Long formulaId, Map<String, Object> context) {
        log.info("执行公式计算, formulaId={}, context={}", formulaId, context);

        // 1. 查询公式
        BillingFormula formula = getFormulaById(formulaId);
        if (formula == null) {
            throw new RuntimeException("公式不存在: " + formulaId);
        }

        if (formula.getStatus() != 1) {
            throw new RuntimeException("公式未启用: " + formula.getFormulaCode());
        }

        // 2. 执行计算
        BigDecimal result = formulaCalculator.calculate(formula, context);

        log.info("公式计算完成, formulaId={}, result={}", formulaId, result);
        return result;
    }
}
