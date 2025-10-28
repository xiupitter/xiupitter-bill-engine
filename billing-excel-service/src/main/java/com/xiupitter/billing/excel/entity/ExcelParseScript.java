package com.xiupitter.billing.excel.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xiupitter.billing.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Excel解析脚本实体
 *
 * @author xiupitter
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("excel_parse_script")
public class ExcelParseScript extends BaseEntity {

    /**
     * 脚本编码
     */
    @TableField("script_code")
    private String scriptCode;

    /**
     * 脚本名称
     */
    @TableField("script_name")
    private String scriptName;

    /**
     * 脚本类型：MVEL, GROOVY, JAVASCRIPT
     */
    @TableField("script_type")
    private String scriptType;

    /**
     * 脚本内容
     */
    @TableField("script_content")
    private String scriptContent;

    /**
     * 脚本描述
     */
    @TableField("description")
    private String description;

    /**
     * 输入参数说明（JSON格式）
     */
    @TableField("input_params")
    private String inputParams;

    /**
     * 输出格式说明（JSON格式）
     */
    @TableField("output_format")
    private String outputFormat;

    /**
     * 版本号
     */
    @TableField("version")
    private Integer version;

    /**
     * 状态：0-禁用, 1-启用
     */
    @TableField("status")
    private Integer status;

    /**
     * 测试数据（JSON格式）
     */
    @TableField("test_data")
    private String testData;

    /**
     * 是否AI生成：0-否, 1-是
     */
    @TableField("ai_generated")
    private Integer aiGenerated;

    /**
     * AI模型名称
     */
    @TableField("ai_model")
    private String aiModel;
}
