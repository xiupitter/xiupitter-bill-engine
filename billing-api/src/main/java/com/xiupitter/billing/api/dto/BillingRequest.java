package com.xiupitter.billing.api.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;

/**
 * 计费请求DTO
 *
 * @author xiupitter
 */
@Data
public class BillingRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 业务单号（订单号、运单号等）
     */
    @NotBlank(message = "业务单号不能为空")
    private String bizNo;

    /**
     * 业务类型
     */
    @NotBlank(message = "业务类型不能为空")
    private String bizType;

    /**
     * 模板编码（可选，不传则自动匹配）
     */
    private String templateCode;

    /**
     * 计费要素值
     * key: 要素编码
     * value: 要素值
     */
    @NotNull(message = "计费要素不能为空")
    private Map<String, Object> factors;

    /**
     * 计费维度值（用于模板匹配）
     * key: 维度编码
     * value: 维度值
     */
    private Map<String, Object> dimensions;

    /**
     * 是否试算（true:试算不保存 false:正式计费并保存）
     */
    private Boolean dryRun = false;

    /**
     * 扩展信息
     */
    private Map<String, Object> extInfo;
}
