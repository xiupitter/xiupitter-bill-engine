package com.xiupitter.billing.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 计费维度编码枚举
 * 
 * 定义了物流计费系统中所有可能的计费维度
 * 计费维度用于匹配计费模板，决定使用哪个计费规则
 *
 * @author xiupitter
 */
@Getter
@AllArgsConstructor
public enum DimensionCodeEnum {

    // ========== 地区维度 ==========
    /**
     * 始发地地区 - 如：华东、华南、华北等
     */
    ORIGIN_REGION("ORIGIN_REGION", "始发地地区", "用于匹配不同地区的计费规则"),
    
    /**
     * 目的地地区 - 如：华东、华南、华北等
     */
    DESTINATION_REGION("DESTINATION_REGION", "目的地地区", "用于匹配不同地区的计费规则"),
    
    /**
     * 始发地省份
     */
    ORIGIN_PROVINCE("ORIGIN_PROVINCE", "始发地省份", "省份级别的地区匹配"),
    
    /**
     * 目的地省份
     */
    DESTINATION_PROVINCE("DESTINATION_PROVINCE", "目的地省份", "省份级别的地区匹配"),
    
    /**
     * 始发地城市
     */
    ORIGIN_CITY("ORIGIN_CITY", "始发地城市", "城市级别的地区匹配"),
    
    /**
     * 目的地城市
     */
    DESTINATION_CITY("DESTINATION_CITY", "目的地城市", "城市级别的地区匹配"),
    
    /**
     * 始发地区域 - 如：市区、郊区、偏远地区
     */
    ORIGIN_AREA("ORIGIN_AREA", "始发地区域", "区域类型的地区匹配"),
    
    /**
     * 目的地区域 - 如：市区、郊区、偏远地区
     */
    DESTINATION_AREA("DESTINATION_AREA", "目的地区域", "区域类型的地区匹配"),
    
    /**
     * 运输路线 - 如：同城、省内、跨省、国际
     */
    TRANSPORT_ROUTE("TRANSPORT_ROUTE", "运输路线", "不同路线的计费规则"),

    // ========== 客户维度 ==========
    /**
     * 客户等级 - 如：VIP、普通、新客户
     */
    CUSTOMER_LEVEL("CUSTOMER_LEVEL", "客户等级", "不同客户等级的计费规则"),
    
    /**
     * 客户类型 - 如：个人、企业、平台
     */
    CUSTOMER_TYPE("CUSTOMER_TYPE", "客户类型", "不同客户类型的计费规则"),
    
    /**
     * 客户行业 - 如：电商、制造业、零售业
     */
    CUSTOMER_INDUSTRY("CUSTOMER_INDUSTRY", "客户行业", "不同行业的计费规则"),
    
    /**
     * 是否签约客户
     */
    IS_CONTRACT_CUSTOMER("IS_CONTRACT_CUSTOMER", "是否签约客户", "签约客户和散客的计费规则"),
    
    /**
     * 月发货量等级 - 如：高、中、低
     */
    MONTHLY_SHIPMENT_LEVEL("MONTHLY_SHIPMENT_LEVEL", "月发货量等级", "根据发货量等级的计费规则"),

    // ========== 商品维度 ==========
    /**
     * 商品类型 - 如：普通货物、易碎品、危险品、液体
     */
    PRODUCT_TYPE("PRODUCT_TYPE", "商品类型", "不同商品类型的计费规则"),
    
    /**
     * 商品类别 - 如：食品、服装、电子产品、家具
     */
    PRODUCT_CATEGORY("PRODUCT_CATEGORY", "商品类别", "不同商品类别的计费规则"),
    
    /**
     * 商品价值等级 - 如：高价值、中价值、低价值
     */
    PRODUCT_VALUE_LEVEL("PRODUCT_VALUE_LEVEL", "商品价值等级", "根据价值等级的计费规则"),

    // ========== 服务维度 ==========
    /**
     * 服务类型 - 如：标准、加急、特快
     */
    SERVICE_TYPE("SERVICE_TYPE", "服务类型", "不同服务类型的计费规则"),
    
    /**
     * 运输方式 - 如：公路、铁路、航空、海运
     */
    TRANSPORT_MODE("TRANSPORT_MODE", "运输方式", "不同运输方式的计费规则"),
    
    /**
     * 配送方式 - 如：自提、送货上门、驿站代收
     */
    DELIVERY_MODE("DELIVERY_MODE", "配送方式", "不同配送方式的计费规则"),
    
    /**
     * 是否需要特殊处理 - 如：易碎、危险品、温控
     */
    SPECIAL_HANDLING("SPECIAL_HANDLING", "特殊处理", "需要特殊处理的计费规则"),

    // ========== 时段维度 ==========
    /**
     * 下单时段 - 如：工作日、周末、节假日
     */
    ORDER_TIME_SLOT("ORDER_TIME_SLOT", "下单时段", "不同时段的计费规则"),
    
    /**
     * 配送时段 - 如：工作日、周末、节假日、夜间
     */
    DELIVERY_TIME_SLOT("DELIVERY_TIME_SLOT", "配送时段", "不同配送时段的计费规则"),
    
    /**
     * 季节 - 如：春季、夏季、秋季、冬季
     */
    SEASON("SEASON", "季节", "不同季节的计费规则"),
    
    /**
     * 高峰期 - 如：双十一、双十二、春节等
     */
    PEAK_PERIOD("PEAK_PERIOD", "高峰期", "高峰期的特殊计费规则"),
    
    /**
     * 时间段 - 如：8:00-18:00、18:00-22:00、22:00-8:00
     */
    TIME_PERIOD("TIME_PERIOD", "时间段", "不同时间段的计费规则"),

    // ========== 仓储维度 ==========
    /**
     * 仓库类型 - 如：普通仓、恒温仓、冷库
     */
    WAREHOUSE_TYPE("WAREHOUSE_TYPE", "仓库类型", "不同仓库类型的计费规则"),
    
    /**
     * 仓储区域 - 如：A区、B区、C区
     */
    STORAGE_ZONE("STORAGE_ZONE", "仓储区域", "不同仓储区域的计费规则"),
    
    /**
     * 货物类型（仓储） - 如：标准货、特殊货、危险品
     */
    STORAGE_GOODS_TYPE("STORAGE_GOODS_TYPE", "货物类型", "仓储中不同货物类型的计费规则"),

    // ========== 渠道维度 ==========
    /**
     * 订单渠道 - 如：官网、APP、小程序、第三方平台
     */
    ORDER_CHANNEL("ORDER_CHANNEL", "订单渠道", "不同渠道的计费规则"),
    
    /**
     * 下单平台 - 如：淘宝、京东、自有平台
     */
    ORDER_PLATFORM("ORDER_PLATFORM", "下单平台", "不同平台的计费规则"),

    // ========== 业务维度 ==========
    /**
     * 业务类型 - 如：电商订单、物流运单、仓储费用、增值服务
     */
    BIZ_TYPE("BIZ_TYPE", "业务类型", "不同业务类型的计费规则"),
    
    /**
     * 订单类型 - 如：普通订单、批量订单、合同订单
     */
    ORDER_TYPE("ORDER_TYPE", "订单类型", "不同订单类型的计费规则"),
    
    /**
     * 支付方式 - 如：在线支付、货到付款、月结
     */
    PAYMENT_METHOD("PAYMENT_METHOD", "支付方式", "不同支付方式的计费规则"),
    
    /**
     * 结算方式 - 如：现结、月结、季结
     */
    SETTLEMENT_METHOD("SETTLEMENT_METHOD", "结算方式", "不同结算方式的计费规则"),

    // ========== 其他维度 ==========
    /**
     * 包装要求 - 如：标准包装、加固包装、定制包装
     */
    PACKAGE_REQUIREMENT("PACKAGE_REQUIREMENT", "包装要求", "不同包装要求的计费规则"),
    
    /**
     * 重量等级 - 如：轻货、中货、重货
     */
    WEIGHT_LEVEL("WEIGHT_LEVEL", "重量等级", "根据重量等级的计费规则"),
    
    /**
     * 体积等级 - 如：小件、中件、大件、超大件
     */
    VOLUME_LEVEL("VOLUME_LEVEL", "体积等级", "根据体积等级的计费规则"),
    
    /**
     * 距离等级 - 如：同城、短途、中途、长途
     */
    DISTANCE_LEVEL("DISTANCE_LEVEL", "距离等级", "根据距离等级的计费规则"),
    
    /**
     * 是否批量订单
     */
    IS_BATCH_ORDER("IS_BATCH_ORDER", "是否批量订单", "批量订单和单个订单的计费规则"),
    
    /**
     * 是否返程订单
     */
    IS_RETURN_ORDER("IS_RETURN_ORDER", "是否返程订单", "返程订单的特殊计费规则"),
    
    /**
     * 是否空运
     */
    IS_AIR_FREIGHT("IS_AIR_FREIGHT", "是否空运", "空运的特殊计费规则"),
    
    /**
     * 是否国际运输
     */
    IS_INTERNATIONAL("IS_INTERNATIONAL", "是否国际运输", "国际运输的特殊计费规则"),
    
    /**
     * 币种 - 如：CNY、USD、EUR
     */
    CURRENCY("CURRENCY", "币种", "不同币种的计费规则"),
    
    /**
     * 温度要求等级 - 如：常温、冷藏、冷冻
     */
    TEMPERATURE_REQUIREMENT("TEMPERATURE_REQUIREMENT", "温度要求等级", "不同温度要求的计费规则"),
    
    /**
     * 时效要求等级 - 如：标准、加急、特快
     */
    URGENCY_LEVEL("URGENCY_LEVEL", "时效要求等级", "不同时效要求的计费规则");

    /**
     * 维度编码
     */
    private final String code;
    
    /**
     * 维度名称
     */
    private final String name;
    
    /**
     * 维度描述
     */
    private final String description;

    /**
     * 根据编码获取枚举
     */
    public static DimensionCodeEnum fromCode(String code) {
        for (DimensionCodeEnum dimension : values()) {
            if (dimension.getCode().equals(code)) {
                return dimension;
            }
        }
        return null;
    }
}

