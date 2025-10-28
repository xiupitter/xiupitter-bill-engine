package com.xiupitter.billing.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 计费公式类型枚举
 *
 * @author xiupitter
 */
@Getter
@AllArgsConstructor
public enum FormulaTypeEnum {

    /**
     * 固定值 - 固定金额
     */
    FIXED("FIXED", "固定值"),

    /**
     * 线性计算 - 基础价格 + 单价 * 数量
     */
    LINEAR("LINEAR", "线性计算"),

    /**
     * 阶梯计算 - 根据区间不同采用不同单价
     */
    LADDER("LADDER", "阶梯计算"),

    /**
     * 分段计算 - 每个区间独立计算后累加
     */
    SEGMENT("SEGMENT", "分段计算"),

    /**
     * 表达式计算 - 使用MVEL表达式
     */
    EXPRESSION("EXPRESSION", "表达式计算"),

    /**
     * 查表计算 - 根据维度组合查询价格表
     */
    TABLE_LOOKUP("TABLE_LOOKUP", "查表计算");

    private final String code;
    private final String desc;

    public static FormulaTypeEnum fromCode(String code) {
        for (FormulaTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
