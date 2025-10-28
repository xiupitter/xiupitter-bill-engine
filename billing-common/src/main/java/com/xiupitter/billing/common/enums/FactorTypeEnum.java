package com.xiupitter.billing.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 计费要素类型枚举
 *
 * @author xiupitter
 */
@Getter
@AllArgsConstructor
public enum FactorTypeEnum {

    /**
     * 数值型 - 如重量、距离、体积等
     */
    NUMERIC("NUMERIC", "数值型"),

    /**
     * 枚举型 - 如地区、时效等级、温控类型等
     */
    ENUM("ENUM", "枚举型"),

    /**
     * 日期型 - 如下单时间、配送日期等
     */
    DATE("DATE", "日期型"),

    /**
     * 布尔型 - 如是否保价、是否加急等
     */
    BOOLEAN("BOOLEAN", "布尔型"),

    /**
     * 文本型 - 如地址、备注等
     */
    TEXT("TEXT", "文本型");

    private final String code;
    private final String desc;

    public static FactorTypeEnum fromCode(String code) {
        for (FactorTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
