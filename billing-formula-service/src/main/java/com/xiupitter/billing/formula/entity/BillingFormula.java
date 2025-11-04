package com.xiupitter.billing.formula.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiupitter.billing.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 计费公式实体
 *
 * 计费公式定义了具体的计算逻辑，支持多种计算模式
 *
 * @author xiupitter
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("billing_formula")
public class BillingFormula extends BaseEntity {

    /**
     * 公式编码（唯一标识）
     */
    private String formulaCode;

    /**
     * 公式名称
     */
    private String formulaName;

    /**
     * 公式类型
     * @see com.xiupitter.billing.common.enums.FormulaTypeEnum
     */
    private String formulaType;

    /**
     * 业务类型
     * @see com.xiupitter.billing.common.enums.BizTypeEnum
     */
    private String bizType;

    /**
     * 公式描述
     */
    private String description;

    /**
     * 公式表达式（MVEL格式）
     * 根据formulaType不同，存储不同内容：
     * - FIXED: 固定值，如 "50.00"
     * - LINEAR: 线性表达式，如 "basePrice + unitPrice * quantity"
     * - LADDER/SEGMENT: 阶梯/分段规则的JSON配置
     * - EXPRESSION: 完整的MVEL表达式
     * - TABLE_LOOKUP: 价格表查询规则
     */
    private String formulaExpression;

    /**
     * 输入要素配置（JSON格式）
     * 定义公式需要哪些输入要素
     * 例如：[{"factorCode":"weight","required":true},{"factorCode":"distance","required":true}]
     */
    private String inputFactors;

    /**
     * 阶梯配置（JSON格式，适用于LADDER和SEGMENT类型）
     * 例如：[
     *   {"start":0,"end":10,"unitPrice":5.0,"fixedPrice":0},
     *   {"start":10,"end":50,"unitPrice":4.0,"fixedPrice":10},
     *   {"start":50,"end":null,"unitPrice":3.0,"fixedPrice":20}
     * ]
     */
    private String ladderConfig;

    /**
     * 价格表配置（JSON格式，适用于TABLE_LOOKUP类型）
     * 支持多维度价格表查询
     */
    private String priceTableConfig;

    /**
     * 结果单位
     */
    private String resultUnit;

    /**
     * 精度（小数位数）
     */
    private Integer precision;

    /**
     * 舍入模式
     * UP: 向上取整
     * DOWN: 向下取整
     * HALF_UP: 四舍五入
     * HALF_DOWN: 五舍六入
     */
    private String roundingMode;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 状态 (0:草稿 1:启用 2:停用)
     */
    private Integer status;
}
