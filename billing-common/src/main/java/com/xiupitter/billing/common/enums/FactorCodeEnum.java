package com.xiupitter.billing.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 计费因子编码枚举
 * 
 * 定义了物流计费系统中所有可能的计费因子
 * 计费因子是参与计费计算的实际数值，用于公式计算
 *
 * @author xiupitter
 */
@Getter
@AllArgsConstructor
public enum FactorCodeEnum {

    // ========== 重量相关 ==========
    /**
     * 重量（kg）- 货物实际重量
     */
    WEIGHT("WEIGHT", "重量", "kg", FactorTypeEnum.NUMERIC),
    
    /**
     * 计费重量（kg）- 体积重量和实际重量的较大值
     */
    CHARGEABLE_WEIGHT("CHARGEABLE_WEIGHT", "计费重量", "kg", FactorTypeEnum.NUMERIC),
    
    /**
     * 体积重量（kg）- 根据体积换算的重量
     */
    VOLUMETRIC_WEIGHT("VOLUMETRIC_WEIGHT", "体积重量", "kg", FactorTypeEnum.NUMERIC),
    
    /**
     * 净重（kg）- 除去包装后的重量
     */
    NET_WEIGHT("NET_WEIGHT", "净重", "kg", FactorTypeEnum.NUMERIC),
    
    /**
     * 毛重（kg）- 包含包装的重量
     */
    GROSS_WEIGHT("GROSS_WEIGHT", "毛重", "kg", FactorTypeEnum.NUMERIC),

    // ========== 距离相关 ==========
    /**
     * 距离（km）- 运输距离
     */
    DISTANCE("DISTANCE", "距离", "km", FactorTypeEnum.NUMERIC),
    
    /**
     * 起始距离（km）- 起始计费距离
     */
    INITIAL_DISTANCE("INITIAL_DISTANCE", "起始距离", "km", FactorTypeEnum.NUMERIC),
    
    /**
     * 超距离（km）- 超出基础距离的部分
     */
    EXCESS_DISTANCE("EXCESS_DISTANCE", "超距离", "km", FactorTypeEnum.NUMERIC),

    // ========== 体积相关 ==========
    /**
     * 体积（m³）- 货物总体积
     */
    VOLUME("VOLUME", "体积", "m³", FactorTypeEnum.NUMERIC),
    
    /**
     * 长度（m）
     */
    LENGTH("LENGTH", "长度", "m", FactorTypeEnum.NUMERIC),
    
    /**
     * 宽度（m）
     */
    WIDTH("WIDTH", "宽度", "m", FactorTypeEnum.NUMERIC),
    
    /**
     * 高度（m）
     */
    HEIGHT("HEIGHT", "高度", "m", FactorTypeEnum.NUMERIC),
    
    /**
     * 面积（m²）
     */
    AREA("AREA", "面积", "m²", FactorTypeEnum.NUMERIC),

    // ========== 数量相关 ==========
    /**
     * 件数（件）- 货物件数
     */
    QUANTITY("QUANTITY", "件数", "件", FactorTypeEnum.NUMERIC),
    
    /**
     * 箱数（箱）
     */
    BOX_COUNT("BOX_COUNT", "箱数", "箱", FactorTypeEnum.NUMERIC),
    
    /**
     * 托盘数（托）
     */
    PALLET_COUNT("PALLET_COUNT", "托盘数", "托", FactorTypeEnum.NUMERIC),
    
    /**
     * 包裹数（个）
     */
    PACKAGE_COUNT("PACKAGE_COUNT", "包裹数", "个", FactorTypeEnum.NUMERIC),

    // ========== 时效相关 ==========
    /**
     * 时效等级 - 如：标准、加急、次日达等
     */
    TIME_TYPE("TIME_TYPE", "时效等级", null, FactorTypeEnum.ENUM),
    
    /**
     * 承诺时效（小时）
     */
    PROMISED_TIME("PROMISED_TIME", "承诺时效", "小时", FactorTypeEnum.NUMERIC),
    
    /**
     * 实际时效（小时）
     */
    ACTUAL_TIME("ACTUAL_TIME", "实际时效", "小时", FactorTypeEnum.NUMERIC),
    
    /**
     * 超时时间（小时）- 超出承诺时效的时间
     */
    OVERTIME("OVERTIME", "超时时间", "小时", FactorTypeEnum.NUMERIC),

    // ========== 价值相关 ==========
    /**
     * 货物价值（元）- 货物申报价值
     */
    GOODS_VALUE("GOODS_VALUE", "货物价值", "元", FactorTypeEnum.NUMERIC),
    
    /**
     * 保价金额（元）- 保价服务的保额
     */
    INSURED_VALUE("INSURED_VALUE", "保价金额", "元", FactorTypeEnum.NUMERIC),
    
    /**
     * 运费金额（元）- 基础运费
     */
    FREIGHT_AMOUNT("FREIGHT_AMOUNT", "运费金额", "元", FactorTypeEnum.NUMERIC),

    // ========== 温度相关（冷链） ==========
    /**
     * 温度要求（℃）- 要求的运输温度
     */
    TEMPERATURE("TEMPERATURE", "温度要求", "℃", FactorTypeEnum.NUMERIC),
    
    /**
     * 温度等级 - 如：常温、冷藏、冷冻
     */
    TEMPERATURE_LEVEL("TEMPERATURE_LEVEL", "温度等级", null, FactorTypeEnum.ENUM),

    // ========== 仓储相关 ==========
    /**
     * 仓储天数（天）
     */
    STORAGE_DAYS("STORAGE_DAYS", "仓储天数", "天", FactorTypeEnum.NUMERIC),
    
    /**
     * 仓储体积（m³）
     */
    STORAGE_VOLUME("STORAGE_VOLUME", "仓储体积", "m³", FactorTypeEnum.NUMERIC),
    
    /**
     * 仓储重量（kg）
     */
    STORAGE_WEIGHT("STORAGE_WEIGHT", "仓储重量", "kg", FactorTypeEnum.NUMERIC),
    
    /**
     * 仓储面积（m²）
     */
    STORAGE_AREA("STORAGE_AREA", "仓储面积", "m²", FactorTypeEnum.NUMERIC),
    
    /**
     * 进仓次数（次）
     */
    INBOUND_COUNT("INBOUND_COUNT", "进仓次数", "次", FactorTypeEnum.NUMERIC),
    
    /**
     * 出仓次数（次）
     */
    OUTBOUND_COUNT("OUTBOUND_COUNT", "出仓次数", "次", FactorTypeEnum.NUMERIC),
    
    /**
     * 库存周转次数（次）
     */
    TURNOVER_COUNT("TURNOVER_COUNT", "库存周转次数", "次", FactorTypeEnum.NUMERIC),

    // ========== 服务相关 ==========
    /**
     * 是否保价
     */
    IS_INSURED("IS_INSURED", "是否保价", null, FactorTypeEnum.BOOLEAN),
    
    /**
     * 是否代收货款
     */
    IS_COD("IS_COD", "是否代收货款", null, FactorTypeEnum.BOOLEAN),
    
    /**
     * 是否上门取件
     */
    IS_PICKUP("IS_PICKUP", "是否上门取件", null, FactorTypeEnum.BOOLEAN),
    
    /**
     * 是否送货上门
     */
    IS_DELIVERY("IS_DELIVERY", "是否送货上门", null, FactorTypeEnum.BOOLEAN),
    
    /**
     * 是否上楼服务
     */
    IS_UPSTAIRS("IS_UPSTAIRS", "是否上楼服务", null, FactorTypeEnum.BOOLEAN),
    
    /**
     * 是否夜间配送
     */
    IS_NIGHT_DELIVERY("IS_NIGHT_DELIVERY", "是否夜间配送", null, FactorTypeEnum.BOOLEAN),
    
    /**
     * 是否节假日配送
     */
    IS_HOLIDAY_DELIVERY("IS_HOLIDAY_DELIVERY", "是否节假日配送", null, FactorTypeEnum.BOOLEAN),
    
    /**
     * 代收货款金额（元）
     */
    COD_AMOUNT("COD_AMOUNT", "代收货款金额", "元", FactorTypeEnum.NUMERIC),
    
    /**
     * 上楼楼层数（层）
     */
    UPSTAIRS_FLOOR("UPSTAIRS_FLOOR", "上楼楼层数", "层", FactorTypeEnum.NUMERIC),

    // ========== 包装相关 ==========
    /**
     * 包装类型 - 如：纸箱、木箱、编织袋等
     */
    PACKAGE_TYPE("PACKAGE_TYPE", "包装类型", null, FactorTypeEnum.ENUM),
    
    /**
     * 包装件数（件）
     */
    PACKAGE_QUANTITY("PACKAGE_QUANTITY", "包装件数", "件", FactorTypeEnum.NUMERIC),
    
    /**
     * 包装费用（元）
     */
    PACKAGE_FEE("PACKAGE_FEE", "包装费用", "元", FactorTypeEnum.NUMERIC),

    // ========== 特殊处理相关 ==========
    /**
     * 是否易碎品
     */
    IS_FRAGILE("IS_FRAGILE", "是否易碎品", null, FactorTypeEnum.BOOLEAN),
    
    /**
     * 是否危险品
     */
    IS_HAZARDOUS("IS_HAZARDOUS", "是否危险品", null, FactorTypeEnum.BOOLEAN),
    
    /**
     * 是否液体
     */
    IS_LIQUID("IS_LIQUID", "是否液体", null, FactorTypeEnum.BOOLEAN),
    
    /**
     * 是否超大件
     */
    IS_OVERSIZED("IS_OVERSIZED", "是否超大件", null, FactorTypeEnum.BOOLEAN),
    
    /**
     * 是否超重件
     */
    IS_OVERWEIGHT("IS_OVERWEIGHT", "是否超重件", null, FactorTypeEnum.BOOLEAN),

    // ========== 日期时间相关 ==========
    /**
     * 下单时间
     */
    ORDER_TIME("ORDER_TIME", "下单时间", null, FactorTypeEnum.DATE),
    
    /**
     * 取件时间
     */
    PICKUP_TIME("PICKUP_TIME", "取件时间", null, FactorTypeEnum.DATE),
    
    /**
     * 发货时间
     */
    SHIP_TIME("SHIP_TIME", "发货时间", null, FactorTypeEnum.DATE),
    
    /**
     * 到货时间
     */
    ARRIVAL_TIME("ARRIVAL_TIME", "到货时间", null, FactorTypeEnum.DATE),
    
    /**
     * 配送时间
     */
    DELIVERY_TIME("DELIVERY_TIME", "配送时间", null, FactorTypeEnum.DATE),
    
    /**
     * 入库时间
     */
    INBOUND_TIME("INBOUND_TIME", "入库时间", null, FactorTypeEnum.DATE),
    
    /**
     * 出库时间
     */
    OUTBOUND_TIME("OUTBOUND_TIME", "出库时间", null, FactorTypeEnum.DATE),

    // ========== 地址相关 ==========
    /**
     * 始发地地址
     */
    ORIGIN_ADDRESS("ORIGIN_ADDRESS", "始发地地址", null, FactorTypeEnum.TEXT),
    
    /**
     * 目的地地址
     */
    DESTINATION_ADDRESS("DESTINATION_ADDRESS", "目的地地址", null, FactorTypeEnum.TEXT),
    
    /**
     * 始发地邮编
     */
    ORIGIN_POSTCODE("ORIGIN_POSTCODE", "始发地邮编", null, FactorTypeEnum.TEXT),
    
    /**
     * 目的地邮编
     */
    DESTINATION_POSTCODE("DESTINATION_POSTCODE", "目的地邮编", null, FactorTypeEnum.TEXT);

    /**
     * 因子编码
     */
    private final String code;
    
    /**
     * 因子名称
     */
    private final String name;
    
    /**
     * 单位
     */
    private final String unit;
    
    /**
     * 因子类型
     */
    private final FactorTypeEnum factorType;

    /**
     * 根据编码获取枚举
     */
    public static FactorCodeEnum fromCode(String code) {
        for (FactorCodeEnum factor : values()) {
            if (factor.getCode().equals(code)) {
                return factor;
            }
        }
        return null;
    }
}

