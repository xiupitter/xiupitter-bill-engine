package com.xiupitter.billing.excel.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xiupitter.billing.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * Excel导入记录实体
 *
 * @author xiupitter
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("excel_import_record")
public class ExcelImportRecord extends BaseEntity {

    /**
     * 导入批次号
     */
    @TableField("import_no")
    private String importNo;

    /**
     * Excel模板配置ID
     */
    @TableField("excel_template_id")
    private Long excelTemplateId;

    /**
     * 文件名
     */
    @TableField("file_name")
    private String fileName;

    /**
     * 文件大小（字节）
     */
    @TableField("file_size")
    private Long fileSize;

    /**
     * 文件存储URL
     */
    @TableField("file_url")
    private String fileUrl;

    /**
     * 总行数
     */
    @TableField("total_rows")
    private Integer totalRows;

    /**
     * 成功行数
     */
    @TableField("success_rows")
    private Integer successRows;

    /**
     * 失败行数
     */
    @TableField("fail_rows")
    private Integer failRows;

    /**
     * 解析结果（JSON格式）
     */
    @TableField("parse_result")
    private String parseResult;

    /**
     * 错误详情（JSON格式）
     */
    @TableField("error_details")
    private String errorDetails;

    /**
     * 导入时间
     */
    @TableField("import_time")
    private LocalDateTime importTime;

    /**
     * 导入状态：0-处理中, 1-成功, 2-部分成功, 3-失败
     */
    @TableField("import_status")
    private Integer importStatus;

    /**
     * 耗时（毫秒）
     */
    @TableField("cost_time")
    private Integer costTime;
}
