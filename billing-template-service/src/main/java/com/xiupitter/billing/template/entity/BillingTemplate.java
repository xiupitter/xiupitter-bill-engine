package com.xiupitter.billing.template.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiupitter.billing.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 计费模板实体
 *
 * 计费模板是一组计费规则的容器，定义了特定业务场景下的计费方式
 * 例如：标准物流计费模板、电商订单计费模板、冷链运输计费模板等
 *
 * @author xiupitter
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("billing_template")
public class BillingTemplate extends BaseEntity {

    /**
     * 模板编码（唯一标识）
     */
    private String templateCode;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 业务类型
     * @see com.xiupitter.billing.common.enums.BizTypeEnum
     */
    private String bizType;

    /**
     * 模板描述
     */
    private String description;

    /**
     * 适用维度配置（JSON格式）
     * 定义该模板在哪些维度条件下生效
     * 例如：{"region":["EAST_CHINA"],"customerLevel":["VIP"]}
     */
    private String dimensionConfig;

    /**
     * 优先级（数字越大优先级越高）
     * 当多个模板同时匹配时，选择优先级最高的
     */
    private Integer priority;

    /**
     * 生效开始时间
     */
    private LocalDateTime effectiveStartTime;

    /**
     * 生效结束时间
     */
    private LocalDateTime effectiveEndTime;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 是否默认模板 (0:否 1:是)
     */
    private Integer isDefault;

    /**
     * 状态 (0:草稿 1:启用 2:停用)
     */
    private Integer status;
}
