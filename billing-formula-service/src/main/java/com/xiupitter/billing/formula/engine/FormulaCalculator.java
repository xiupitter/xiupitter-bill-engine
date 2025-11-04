package com.xiupitter.billing.formula.engine;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.xiupitter.billing.common.enums.FormulaTypeEnum;
import com.xiupitter.billing.formula.entity.BillingFormula;
import lombok.extern.slf4j.Slf4j;
import org.mvel2.MVEL;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * 公式计算器
 *
 * 核心计算引擎，支持多种计算模式：
 * 1. 固定值
 * 2. 线性计算
 * 3. 阶梯计费
 * 4. 分段计费
 * 5. MVEL表达式计算
 * 6. 查表计费
 *
 * @author xiupitter
 */
@Slf4j
@Component
public class FormulaCalculator {

    /**
     * 执行公式计算
     *
     * @param formula 计费公式
     * @param context 计算上下文（包含所有要素值）
     * @return 计算结果
     */
    public BigDecimal calculate(BillingFormula formula, Map<String, Object> context) {
        try {
            FormulaTypeEnum formulaType = FormulaTypeEnum.fromCode(formula.getFormulaType());
            if (formulaType == null) {
                throw new IllegalArgumentException("不支持的公式类型: " + formula.getFormulaType());
            }

            BigDecimal result;
            switch (formulaType) {
                case FIXED:
                    result = calculateFixed(formula, context);
                    break;
                case LINEAR:
                    result = calculateLinear(formula, context);
                    break;
                case LADDER:
                    result = calculateLadder(formula, context);
                    break;
                case SEGMENT:
                    result = calculateSegment(formula, context);
                    break;
                case EXPRESSION:
                    result = calculateExpression(formula, context);
                    break;
                case TABLE_LOOKUP:
                    result = calculateTableLookup(formula, context);
                    break;
                default:
                    throw new IllegalArgumentException("不支持的公式类型: " + formulaType);
            }

            // 应用精度和舍入模式
            return applyPrecision(result, formula.getPrecision(), formula.getRoundingMode());

        } catch (Exception e) {
            log.error("公式计算失败, formulaCode={}, context={}", formula.getFormulaCode(), context, e);
            throw new RuntimeException("公式计算失败: " + e.getMessage(), e);
        }
    }

    /**
     * 固定值计算
     */
    private BigDecimal calculateFixed(BillingFormula formula, Map<String, Object> context) {
        return new BigDecimal(formula.getFormulaExpression());
    }

    /**
     * 线性计算
     * 表达式示例: "basePrice + unitPrice * quantity"
     */
    private BigDecimal calculateLinear(BillingFormula formula, Map<String, Object> context) {
        Object result = MVEL.eval(formula.getFormulaExpression(), context);
        return convertToBigDecimal(result);
    }

    /**
     * 阶梯计费
     * 根据数量所在区间，使用对应区间的单价计算
     * 示例：0-10kg: 5元/kg, 10-50kg: 4元/kg, >50kg: 3元/kg
     * 如果重量为30kg，则费用 = 30 * 4 = 120元
     */
    private BigDecimal calculateLadder(BillingFormula formula, Map<String, Object> context) {
        JSONArray ladderConfig = JSON.parseArray(formula.getLadderConfig());

        // 获取计量要素（默认取第一个输入要素）
        JSONArray inputFactors = JSON.parseArray(formula.getInputFactors());
        String factorCode = inputFactors.getJSONObject(0).getString("factorCode");
        BigDecimal quantity = getBigDecimal(context.get(factorCode));

        // 查找匹配的阶梯
        for (int i = 0; i < ladderConfig.size(); i++) {
            JSONObject ladder = ladderConfig.getJSONObject(i);
            BigDecimal start = ladder.getBigDecimal("start");
            BigDecimal end = ladder.getBigDecimal("end");

            if (isInRange(quantity, start, end)) {
                BigDecimal unitPrice = ladder.getBigDecimal("unitPrice");
                BigDecimal fixedPrice = ladder.getBigDecimal("fixedPrice");

                // 阶梯计费 = 固定价格 + 单价 * 数量
                return fixedPrice.add(unitPrice.multiply(quantity));
            }
        }

        throw new IllegalArgumentException("未找到匹配的阶梯配置, quantity=" + quantity);
    }

    /**
     * 分段计费
     * 将数量分段，每段使用各自的单价，最后累加
     * 示例：0-10kg: 5元/kg, 10-50kg: 4元/kg, >50kg: 3元/kg
     * 如果重量为60kg，则费用 = 10*5 + 40*4 + 10*3 = 50 + 160 + 30 = 240元
     */
    private BigDecimal calculateSegment(BillingFormula formula, Map<String, Object> context) {
        JSONArray ladderConfig = JSON.parseArray(formula.getLadderConfig());

        // 获取计量要素
        JSONArray inputFactors = JSON.parseArray(formula.getInputFactors());
        String factorCode = inputFactors.getJSONObject(0).getString("factorCode");
        BigDecimal quantity = getBigDecimal(context.get(factorCode));

        BigDecimal totalFee = BigDecimal.ZERO;
        BigDecimal remainQuantity = quantity;

        for (int i = 0; i < ladderConfig.size(); i++) {
            JSONObject segment = ladderConfig.getJSONObject(i);
            BigDecimal start = segment.getBigDecimal("start");
            BigDecimal end = segment.getBigDecimal("end");
            BigDecimal unitPrice = segment.getBigDecimal("unitPrice");
            BigDecimal fixedPrice = segment.getBigDecimal("fixedPrice");

            if (remainQuantity.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            // 计算本段的数量
            BigDecimal segmentSize = (end == null) ? remainQuantity :
                                      end.subtract(start).min(remainQuantity);

            // 本段费用 = 固定价格 + 单价 * 本段数量
            BigDecimal segmentFee = fixedPrice.add(unitPrice.multiply(segmentSize));
            totalFee = totalFee.add(segmentFee);

            remainQuantity = remainQuantity.subtract(segmentSize);
        }

        return totalFee;
    }

    /**
     * 表达式计算
     * 使用MVEL引擎计算复杂表达式
     */
    private BigDecimal calculateExpression(BillingFormula formula, Map<String, Object> context) {
        // 添加常用数学函数到上下文
        Map<String, Object> enrichedContext = new HashMap<>(context);
        enrichedContext.put("Math", Math.class);
        enrichedContext.put("BigDecimal", BigDecimal.class);

        Object result = MVEL.eval(formula.getFormulaExpression(), enrichedContext);
        return convertToBigDecimal(result);
    }

    /**
     * 查表计费
     * 根据多维度条件查询价格表
     */
    private BigDecimal calculateTableLookup(BillingFormula formula, Map<String, Object> context) {
        JSONObject priceTable = JSON.parseObject(formula.getPriceTableConfig());
        JSONArray rules = priceTable.getJSONArray("rules");

        // 遍历规则，找到第一个匹配的
        for (int i = 0; i < rules.size(); i++) {
            JSONObject rule = rules.getJSONObject(i);
            JSONObject conditions = rule.getJSONObject("conditions");

            // 检查所有条件是否匹配
            if (matchConditions(conditions, context)) {
                return rule.getBigDecimal("price");
            }
        }

        // 如果没有匹配，使用默认价格
        return priceTable.getBigDecimal("defaultPrice");
    }

    /**
     * 检查条件是否匹配
     */
    private boolean matchConditions(JSONObject conditions, Map<String, Object> context) {
        for (String key : conditions.keySet()) {
            Object expectedValue = conditions.get(key);
            Object actualValue = context.get(key);

            if (!matchValue(expectedValue, actualValue)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 匹配值
     */
    private boolean matchValue(Object expected, Object actual) {
        if (expected == null && actual == null) {
            return true;
        }
        if (expected == null || actual == null) {
            return false;
        }

        // 支持范围匹配
        if (expected instanceof JSONObject) {
            JSONObject range = (JSONObject) expected;
            BigDecimal value = getBigDecimal(actual);
            BigDecimal min = range.getBigDecimal("min");
            BigDecimal max = range.getBigDecimal("max");

            return isInRange(value, min, max);
        }

        return expected.equals(actual);
    }

    /**
     * 判断值是否在范围内
     */
    private boolean isInRange(BigDecimal value, BigDecimal start, BigDecimal end) {
        boolean afterStart = (start == null) || value.compareTo(start) >= 0;
        boolean beforeEnd = (end == null) || value.compareTo(end) < 0;
        return afterStart && beforeEnd;
    }

    /**
     * 转换为BigDecimal
     */
    private BigDecimal getBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Number) {
            return new BigDecimal(value.toString());
        }
        return new BigDecimal(value.toString());
    }

    /**
     * 转换结果为BigDecimal
     */
    private BigDecimal convertToBigDecimal(Object result) {
        if (result instanceof BigDecimal) {
            return (BigDecimal) result;
        }
        if (result instanceof Number) {
            return new BigDecimal(result.toString());
        }
        return new BigDecimal(result.toString());
    }

    /**
     * 应用精度和舍入模式
     */
    private BigDecimal applyPrecision(BigDecimal value, Integer precision, String roundingModeStr) {
        if (precision == null) {
            precision = 2; // 默认2位小数
        }

        RoundingMode roundingMode = RoundingMode.HALF_UP; // 默认四舍五入
        if (roundingModeStr != null) {
            try {
                roundingMode = RoundingMode.valueOf(roundingModeStr);
            } catch (IllegalArgumentException e) {
                log.warn("无效的舍入模式: {}, 使用默认值HALF_UP", roundingModeStr);
            }
        }

        return value.setScale(precision, roundingMode);
    }
}
