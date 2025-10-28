package com.xiupitter.billing.engine.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiupitter.billing.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 计费记录实体
 *
 * @author xiupitter
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("billing_record")
public class BillingRecord extends BaseEntity {

    /**
     * 业务单号（订单号、运单号等）
     */
    private String bizNo;

    /**
     * 业务类型
     */
    private String bizType;

    /**
     * 使用的模板编码
     */
    private String templateCode;

    /**
     * 计费要素（JSON格式）
     */
    private String factors;

    /**
     * 计费维度（JSON格式）
     */
    private String dimensions;

    /**
     * 总费用
     */
    private BigDecimal totalFee;

    /**
     * 币种
     */
    private String currency;

    /**
     * 计费项明细（JSON格式）
     */
    private String itemDetails;

    /**
     * 计费时间
     */
    private LocalDateTime billingTime;

    /**
     * 状态 (0:计费失败 1:计费成功)
     */
    private Integer status;

    /**
     * 失败原因
     */
    private String failReason;
}
