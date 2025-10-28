package com.xiupitter.billing.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务类型枚举
 *
 * @author xiupitter
 */
@Getter
@AllArgsConstructor
public enum BizTypeEnum {

    /**
     * 电商订单
     */
    E_COMMERCE_ORDER("E_COMMERCE_ORDER", "电商订单"),

    /**
     * 物流运单
     */
    LOGISTICS_WAYBILL("LOGISTICS_WAYBILL", "物流运单"),

    /**
     * 仓储费用
     */
    WAREHOUSE_FEE("WAREHOUSE_FEE", "仓储费用"),

    /**
     * 增值服务
     */
    VALUE_ADDED_SERVICE("VALUE_ADDED_SERVICE", "增值服务");

    private final String code;
    private final String desc;

    public static BizTypeEnum fromCode(String code) {
        for (BizTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
