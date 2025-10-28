package com.xiupitter.billing.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 计费结果DTO
 *
 * @author xiupitter
 */
@Data
public class BillingResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 业务单号
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
     * 使用的模板名称
     */
    private String templateName;

    /**
     * 计费项明细
     */
    private List<BillingItemResult> items;

    /**
     * 总费用
     */
    private BigDecimal totalFee;

    /**
     * 币种
     */
    private String currency = "CNY";

    /**
     * 计费时间
     */
    private Long billingTime;

    /**
     * 是否试算
     */
    private Boolean dryRun;

    /**
     * 计费记录ID（正式计费时才有）
     */
    private Long billingRecordId;

    /**
     * 计费项明细
     */
    @Data
    public static class BillingItemResult implements Serializable {

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
         * 使用的公式编码
         */
        private String formulaCode;

        /**
         * 使用的公式名称
         */
        private String formulaName;

        /**
         * 计算公式描述
         */
        private String formulaDescription;

        /**
         * 输入参数
         */
        private Map<String, Object> inputParams;

        /**
         * 计算结果
         */
        private BigDecimal amount;

        /**
         * 计算过程（调试用）
         */
        private String calculationProcess;
    }
}
