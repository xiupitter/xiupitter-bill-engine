package com.xiupitter.billing.factor.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiupitter.billing.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 计费要素实体
 *
 * 计费要素是计费的最小计算单元，定义了参与计费的各种维度
 * 例如：重量、距离、体积、时效、地区等
 *
 * @author xiupitter
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("billing_factor")
public class BillingFactor extends BaseEntity {

    /**
     * 要素编码（唯一标识）
     * 例如：WEIGHT, DISTANCE, VOLUME, REGION, TIME_TYPE
     */
    private String factorCode;

    /**
     * 要素名称
     */
    private String factorName;

    /**
     * 要素类型
     * @see com.xiupitter.billing.common.enums.FactorTypeEnum
     */
    private String factorType;

    /**
     * 业务类型
     * @see com.xiupitter.billing.common.enums.BizTypeEnum
     */
    private String bizType;

    /**
     * 数据单位（针对数值型）
     * 例如：kg, km, m³
     */
    private String unit;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 是否必填 (0:否 1:是)
     */
    private Integer required;

    /**
     * 验证规则（正则表达式）
     */
    private String validationRule;

    /**
     * 枚举值配置（JSON格式，针对枚举型）
     * 例如：[{"code":"NORMAL","name":"普通"},{"code":"EXPRESS","name":"加急"}]
     */
    private String enumConfig;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 状态 (0:禁用 1:启用)
     */
    private Integer status;
}
