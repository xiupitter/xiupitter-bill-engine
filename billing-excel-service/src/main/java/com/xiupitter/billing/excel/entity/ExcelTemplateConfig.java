package com.xiupitter.billing.excel.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xiupitter.billing.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Excel模板配置实体
 *
 * @author xiupitter
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("excel_template_config")
public class ExcelTemplateConfig extends BaseEntity {

    /**
     * 模板编码
     */
    @TableField("template_code")
    private String templateCode;

    /**
     * 模板名称
     */
    @TableField("template_name")
    private String templateName;

    /**
     * 业务类型
     */
    @TableField("biz_type")
    private String bizType;

    /**
     * 文件名匹配模式（正则表达式）
     */
    @TableField("file_name_pattern")
    private String fileNamePattern;

    /**
     * Sheet名称
     */
    @TableField("sheet_name")
    private String sheetName;

    /**
     * 表头所在行号（从1开始）
     */
    @TableField("header_row")
    private Integer headerRow;

    /**
     * 数据开始行号（从1开始）
     */
    @TableField("data_start_row")
    private Integer dataStartRow;

    /**
     * 关联的解析脚本ID
     */
    @TableField("parse_script_id")
    private Long parseScriptId;

    /**
     * 列映射配置（JSON格式）
     */
    @TableField("column_mapping")
    private String columnMapping;

    /**
     * 数据验证规则（JSON格式）
     */
    @TableField("validation_rules")
    private String validationRules;

    /**
     * 示例文件URL
     */
    @TableField("sample_file_url")
    private String sampleFileUrl;

    /**
     * 优先级
     */
    @TableField("priority")
    private Integer priority;

    /**
     * 状态：0-禁用, 1-启用
     */
    @TableField("status")
    private Integer status;
}
