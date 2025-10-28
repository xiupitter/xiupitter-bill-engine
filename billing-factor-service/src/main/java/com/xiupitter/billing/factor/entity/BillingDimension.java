package com.xiupitter.billing.factor.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiupitter.billing.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 计费维度实体
 *
 * 计费维度定义了影响计费的条件维度，用于计费规则的匹配
 * 例如：地区、客户等级、商品类型、时段等
 *
 * @author xiupitter
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("billing_dimension")
public class BillingDimension extends BaseEntity {

    /**
     * 维度编码（唯一标识）
     * 例如：REGION, CUSTOMER_LEVEL, PRODUCT_TYPE, TIME_SLOT
     */
    private String dimensionCode;

    /**
     * 维度名称
     */
    private String dimensionName;

    /**
     * 业务类型
     * @see com.xiupitter.billing.common.enums.BizTypeEnum
     */
    private String bizType;

    /**
     * 父维度ID（支持层级维度）
     */
    private Long parentId;

    /**
     * 维度层级
     */
    private Integer level;

    /**
     * 维度值配置（JSON格式）
     * 例如：[{"code":"EAST_CHINA","name":"华东","children":[...]}]
     */
    private String dimensionValues;

    /**
     * 是否支持多选 (0:否 1:是)
     */
    private Integer multiSelect;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 状态 (0:禁用 1:启用)
     */
    private Integer status;
}
