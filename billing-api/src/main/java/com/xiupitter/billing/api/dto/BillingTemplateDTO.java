package com.xiupitter.billing.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 计费模板DTO（带计费项列表）
 *
 * @author xiupitter
 */
@Data
public class BillingTemplateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 模板ID
     */
    private Long id;

    /**
     * 模板编码
     */
    private String templateCode;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 业务类型
     */
    private String bizType;

    /**
     * 模板描述
     */
    private String description;

    /**
     * 适用维度配置
     */
    private String dimensionConfig;

    /**
     * 优先级
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
     * 是否默认模板
     */
    private Integer isDefault;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 计费项列表
     */
    private List<BillingItemDTO> items;

    /**
     * 计费项DTO
     */
    @Data
    public static class BillingItemDTO implements Serializable {

        /**
         * 项目ID
         */
        private Long id;

        /**
         * 项目编码
         */
        private String itemCode;

        /**
         * 项目名称
         */
        private String itemName;

        /**
         * 项目类型
         */
        private String itemType;

        /**
         * 关联的计费公式ID
         */
        private Long formulaId;

        /**
         * 是否必须计算
         */
        private Integer required;

        /**
         * 计算条件表达式
         */
        private String calculateCondition;

        /**
         * 排序
         */
        private Integer sortOrder;

        /**
         * 状态
         */
        private Integer status;
    }
}
