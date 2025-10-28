package com.xiupitter.billing.template.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiupitter.billing.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 计费项目实体
 *
 * 计费项目定义了具体的收费项，每个项目关联一个计费公式
 * 例如：运费、保价费、包装费、仓储费等
 *
 * @author xiupitter
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("billing_item")
public class BillingItem extends BaseEntity {

    /**
     * 所属模板ID
     */
    private Long templateId;

    /**
     * 项目编码（唯一标识）
     */
    private String itemCode;

    /**
     * 项目名称
     */
    private String itemName;

    /**
     * 项目类型
     * MAIN: 主费用
     * ADDITIONAL: 附加费用
     * DISCOUNT: 折扣
     * SURCHARGE: 附加费
     */
    private String itemType;

    /**
     * 关联的计费公式ID
     */
    private Long formulaId;

    /**
     * 是否必须计算 (0:否 1:是)
     */
    private Integer required;

    /**
     * 最小值
     */
    private BigDecimal minValue;

    /**
     * 最大值
     */
    private BigDecimal maxValue;

    /**
     * 计算条件表达式（MVEL）
     * 用于判断该项目是否需要计算
     * 例如："weight > 100 && region == 'REMOTE'"
     */
    private String calculateCondition;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 状态 (0:禁用 1:启用)
     */
    private Integer status;
}
