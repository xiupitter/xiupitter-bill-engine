-- ========================================
-- xiupitter计费引擎 - 数据库表结构
-- ========================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `xiupitter_billing` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `xiupitter_billing`;

-- ========================================
-- 1. 计费要素表
-- ========================================
DROP TABLE IF EXISTS `billing_factor`;
CREATE TABLE `billing_factor` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `factor_code` VARCHAR(64) NOT NULL COMMENT '要素编码',
    `factor_name` VARCHAR(128) NOT NULL COMMENT '要素名称',
    `factor_type` VARCHAR(32) NOT NULL COMMENT '要素类型：NUMERIC-数值型, ENUM-枚举型, DATE-日期型, BOOLEAN-布尔型, TEXT-文本型',
    `biz_type` VARCHAR(32) NOT NULL COMMENT '业务类型：E_COMMERCE_ORDER-电商订单, LOGISTICS_WAYBILL-物流运单, WAREHOUSE_FEE-仓储费用',
    `unit` VARCHAR(16) COMMENT '数据单位（针对数值型）',
    `default_value` VARCHAR(128) COMMENT '默认值',
    `required` TINYINT(1) DEFAULT 0 COMMENT '是否必填：0-否, 1-是',
    `validation_rule` VARCHAR(512) COMMENT '验证规则（正则表达式）',
    `enum_config` TEXT COMMENT '枚举值配置（JSON格式）',
    `sort_order` INT(11) DEFAULT 0 COMMENT '排序',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态：0-禁用, 1-启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` VARCHAR(64) COMMENT '创建人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by` VARCHAR(64) COMMENT '更新人',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '删除标记：0-未删除, 1-已删除',
    `remark` VARCHAR(512) COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_factor_code` (`factor_code`),
    KEY `idx_biz_type` (`biz_type`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='计费要素表';

-- ========================================
-- 2. 计费维度表
-- ========================================
DROP TABLE IF EXISTS `billing_dimension`;
CREATE TABLE `billing_dimension` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `dimension_code` VARCHAR(64) NOT NULL COMMENT '维度编码',
    `dimension_name` VARCHAR(128) NOT NULL COMMENT '维度名称',
    `biz_type` VARCHAR(32) NOT NULL COMMENT '业务类型',
    `parent_id` BIGINT(20) DEFAULT 0 COMMENT '父维度ID',
    `level` INT(11) DEFAULT 1 COMMENT '维度层级',
    `dimension_values` TEXT COMMENT '维度值配置（JSON格式）',
    `multi_select` TINYINT(1) DEFAULT 0 COMMENT '是否支持多选：0-否, 1-是',
    `sort_order` INT(11) DEFAULT 0 COMMENT '排序',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态：0-禁用, 1-启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` VARCHAR(64) COMMENT '创建人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by` VARCHAR(64) COMMENT '更新人',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '删除标记：0-未删除, 1-已删除',
    `remark` VARCHAR(512) COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dimension_code` (`dimension_code`),
    KEY `idx_biz_type` (`biz_type`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='计费维度表';

-- ========================================
-- 3. 计费公式表
-- ========================================
DROP TABLE IF EXISTS `billing_formula`;
CREATE TABLE `billing_formula` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `formula_code` VARCHAR(64) NOT NULL COMMENT '公式编码',
    `formula_name` VARCHAR(128) NOT NULL COMMENT '公式名称',
    `formula_type` VARCHAR(32) NOT NULL COMMENT '公式类型：FIXED-固定值, LINEAR-线性计算, LADDER-阶梯计算, SEGMENT-分段计算, EXPRESSION-表达式计算, TABLE_LOOKUP-查表计算',
    `biz_type` VARCHAR(32) NOT NULL COMMENT '业务类型',
    `description` VARCHAR(512) COMMENT '公式描述',
    `formula_expression` TEXT COMMENT '公式表达式',
    `input_factors` TEXT COMMENT '输入要素配置（JSON格式）',
    `ladder_config` TEXT COMMENT '阶梯配置（JSON格式）',
    `price_table_config` TEXT COMMENT '价格表配置（JSON格式）',
    `result_unit` VARCHAR(16) COMMENT '结果单位',
    `precision` INT(11) DEFAULT 2 COMMENT '精度（小数位数）',
    `rounding_mode` VARCHAR(16) DEFAULT 'HALF_UP' COMMENT '舍入模式：UP-向上取整, DOWN-向下取整, HALF_UP-四舍五入, HALF_DOWN-五舍六入',
    `version` INT(11) DEFAULT 1 COMMENT '版本号',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态：0-草稿, 1-启用, 2-停用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` VARCHAR(64) COMMENT '创建人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by` VARCHAR(64) COMMENT '更新人',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '删除标记：0-未删除, 1-已删除',
    `remark` VARCHAR(512) COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_formula_code` (`formula_code`),
    KEY `idx_biz_type` (`biz_type`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='计费公式表';

-- ========================================
-- 4. 计费模板表
-- ========================================
DROP TABLE IF EXISTS `billing_template`;
CREATE TABLE `billing_template` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `template_code` VARCHAR(64) NOT NULL COMMENT '模板编码',
    `template_name` VARCHAR(128) NOT NULL COMMENT '模板名称',
    `biz_type` VARCHAR(32) NOT NULL COMMENT '业务类型',
    `description` VARCHAR(512) COMMENT '模板描述',
    `dimension_config` TEXT COMMENT '适用维度配置（JSON格式）',
    `priority` INT(11) DEFAULT 0 COMMENT '优先级（数字越大优先级越高）',
    `effective_start_time` DATETIME COMMENT '生效开始时间',
    `effective_end_time` DATETIME COMMENT '生效结束时间',
    `version` INT(11) DEFAULT 1 COMMENT '版本号',
    `is_default` TINYINT(1) DEFAULT 0 COMMENT '是否默认模板：0-否, 1-是',
    `status` TINYINT(1) DEFAULT 0 COMMENT '状态：0-草稿, 1-启用, 2-停用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` VARCHAR(64) COMMENT '创建人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by` VARCHAR(64) COMMENT '更新人',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '删除标记：0-未删除, 1-已删除',
    `remark` VARCHAR(512) COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_template_code` (`template_code`),
    KEY `idx_biz_type` (`biz_type`),
    KEY `idx_priority` (`priority`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='计费模板表';

-- ========================================
-- 5. 计费项目表
-- ========================================
DROP TABLE IF EXISTS `billing_item`;
CREATE TABLE `billing_item` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `template_id` BIGINT(20) NOT NULL COMMENT '所属模板ID',
    `item_code` VARCHAR(64) NOT NULL COMMENT '项目编码',
    `item_name` VARCHAR(128) NOT NULL COMMENT '项目名称',
    `item_type` VARCHAR(32) NOT NULL COMMENT '项目类型：MAIN-主费用, ADDITIONAL-附加费用, DISCOUNT-折扣, SURCHARGE-附加费',
    `formula_id` BIGINT(20) NOT NULL COMMENT '关联的计费公式ID',
    `required` TINYINT(1) DEFAULT 0 COMMENT '是否必须计算：0-否, 1-是',
    `min_value` DECIMAL(18, 4) COMMENT '最小值',
    `max_value` DECIMAL(18, 4) COMMENT '最大值',
    `calculate_condition` VARCHAR(512) COMMENT '计算条件表达式（MVEL）',
    `sort_order` INT(11) DEFAULT 0 COMMENT '排序',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态：0-禁用, 1-启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` VARCHAR(64) COMMENT '创建人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by` VARCHAR(64) COMMENT '更新人',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '删除标记：0-未删除, 1-已删除',
    `remark` VARCHAR(512) COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_template_id` (`template_id`),
    KEY `idx_formula_id` (`formula_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='计费项目表';

-- ========================================
-- 6. 计费记录表
-- ========================================
DROP TABLE IF EXISTS `billing_record`;
CREATE TABLE `billing_record` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `biz_no` VARCHAR(64) NOT NULL COMMENT '业务单号',
    `biz_type` VARCHAR(32) NOT NULL COMMENT '业务类型',
    `template_code` VARCHAR(64) COMMENT '使用的模板编码',
    `factors` TEXT COMMENT '计费要素（JSON格式）',
    `dimensions` TEXT COMMENT '计费维度（JSON格式）',
    `total_fee` DECIMAL(18, 4) NOT NULL COMMENT '总费用',
    `currency` VARCHAR(8) DEFAULT 'CNY' COMMENT '币种',
    `item_details` TEXT COMMENT '计费项明细（JSON格式）',
    `billing_time` DATETIME NOT NULL COMMENT '计费时间',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态：0-计费失败, 1-计费成功',
    `fail_reason` VARCHAR(512) COMMENT '失败原因',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` VARCHAR(64) COMMENT '创建人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by` VARCHAR(64) COMMENT '更新人',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '删除标记：0-未删除, 1-已删除',
    `remark` VARCHAR(512) COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_biz_no` (`biz_no`),
    KEY `idx_biz_type` (`biz_type`),
    KEY `idx_billing_time` (`billing_time`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='计费记录表';

-- ========================================
-- 示例数据
-- ========================================

-- 插入示例计费要素
INSERT INTO `billing_factor` (`factor_code`, `factor_name`, `factor_type`, `biz_type`, `unit`, `required`, `sort_order`) VALUES
('WEIGHT', '重量', 'NUMERIC', 'LOGISTICS_WAYBILL', 'kg', 1, 1),
('DISTANCE', '距离', 'NUMERIC', 'LOGISTICS_WAYBILL', 'km', 1, 2),
('VOLUME', '体积', 'NUMERIC', 'LOGISTICS_WAYBILL', 'm³', 0, 3),
('REGION', '地区', 'ENUM', 'LOGISTICS_WAYBILL', NULL, 1, 4),
('TIME_TYPE', '时效类型', 'ENUM', 'LOGISTICS_WAYBILL', NULL, 1, 5);

-- 插入示例计费公式 - 固定值
INSERT INTO `billing_formula` (`formula_code`, `formula_name`, `formula_type`, `biz_type`, `description`, `formula_expression`, `precision`) VALUES
('FIXED_BASE_FEE', '固定基础运费', 'FIXED', 'LOGISTICS_WAYBILL', '固定10元基础运费', '10.00', 2);

-- 插入示例计费公式 - 线性计算
INSERT INTO `billing_formula` (`formula_code`, `formula_name`, `formula_type`, `biz_type`, `description`, `formula_expression`, `input_factors`, `precision`) VALUES
('LINEAR_WEIGHT_FEE', '重量线性计费', 'LINEAR', 'LOGISTICS_WAYBILL', '重量 * 单价', 'weight * 5.0', '[{"factorCode":"weight","required":true}]', 2);

-- 插入示例计费公式 - 阶梯计费
INSERT INTO `billing_formula` (`formula_code`, `formula_name`, `formula_type`, `biz_type`, `description`, `input_factors`, `ladder_config`, `precision`) VALUES
('LADDER_WEIGHT_FEE', '重量阶梯计费', 'LADDER', 'LOGISTICS_WAYBILL', '0-10kg:5元/kg, 10-50kg:4元/kg, >50kg:3元/kg',
'[{"factorCode":"weight","required":true}]',
'[{"start":0,"end":10,"unitPrice":5.0,"fixedPrice":0},{"start":10,"end":50,"unitPrice":4.0,"fixedPrice":10},{"start":50,"end":null,"unitPrice":3.0,"fixedPrice":20}]',
2);

-- 插入示例计费模板
INSERT INTO `billing_template` (`template_code`, `template_name`, `biz_type`, `description`, `priority`, `is_default`, `status`) VALUES
('STANDARD_LOGISTICS', '标准物流计费模板', 'LOGISTICS_WAYBILL', '适用于普通物流运单', 10, 1, 1);

-- 插入示例计费项目
INSERT INTO `billing_item` (`template_id`, `item_code`, `item_name`, `item_type`, `formula_id`, `required`, `sort_order`) VALUES
(1, 'BASE_FEE', '基础运费', 'MAIN', 1, 1, 1),
(1, 'WEIGHT_FEE', '重量费用', 'ADDITIONAL', 3, 1, 2);

-- ========================================
-- 7. Excel解析脚本表
-- ========================================
DROP TABLE IF EXISTS `excel_parse_script`;
CREATE TABLE `excel_parse_script` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `script_code` VARCHAR(64) NOT NULL COMMENT '脚本编码',
    `script_name` VARCHAR(128) NOT NULL COMMENT '脚本名称',
    `script_type` VARCHAR(32) DEFAULT 'MVEL' COMMENT '脚本类型：MVEL, GROOVY, JAVASCRIPT',
    `script_content` TEXT NOT NULL COMMENT 'MVEL脚本内容',
    `description` VARCHAR(512) COMMENT '脚本描述',
    `input_params` TEXT COMMENT '输入参数说明（JSON格式）',
    `output_format` TEXT COMMENT '输出格式说明（JSON格式）',
    `version` INT(11) DEFAULT 1 COMMENT '版本号',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态：0-禁用, 1-启用',
    `test_data` TEXT COMMENT '测试数据（JSON格式）',
    `ai_generated` TINYINT(1) DEFAULT 0 COMMENT '是否AI生成：0-否, 1-是',
    `ai_model` VARCHAR(64) COMMENT 'AI模型名称（如：通义千问、GPT-4）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` VARCHAR(64) COMMENT '创建人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by` VARCHAR(64) COMMENT '更新人',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '删除标记：0-未删除, 1-已删除',
    `remark` VARCHAR(512) COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_script_code` (`script_code`),
    KEY `idx_status` (`status`),
    KEY `idx_version` (`version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Excel解析脚本表';

-- ========================================
-- 8. Excel模板配置表
-- ========================================
DROP TABLE IF EXISTS `excel_template_config`;
CREATE TABLE `excel_template_config` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `template_code` VARCHAR(64) NOT NULL COMMENT '模板编码',
    `template_name` VARCHAR(128) NOT NULL COMMENT '模板名称',
    `biz_type` VARCHAR(32) NOT NULL COMMENT '业务类型：LOGISTICS_WAYBILL-物流运单, E_COMMERCE_ORDER-电商订单等',
    `file_name_pattern` VARCHAR(256) COMMENT '文件名匹配模式（正则表达式）',
    `sheet_name` VARCHAR(128) COMMENT 'Sheet名称（可为空表示第一个Sheet）',
    `header_row` INT(11) DEFAULT 1 COMMENT '表头所在行号（从1开始）',
    `data_start_row` INT(11) DEFAULT 2 COMMENT '数据开始行号（从1开始）',
    `parse_script_id` BIGINT(20) NOT NULL COMMENT '关联的解析脚本ID',
    `column_mapping` TEXT COMMENT '列映射配置（JSON格式）',
    `validation_rules` TEXT COMMENT '数据验证规则（JSON格式）',
    `sample_file_url` VARCHAR(512) COMMENT '示例文件URL',
    `priority` INT(11) DEFAULT 0 COMMENT '优先级（数字越大优先级越高）',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态：0-禁用, 1-启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` VARCHAR(64) COMMENT '创建人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by` VARCHAR(64) COMMENT '更新人',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '删除标记：0-未删除, 1-已删除',
    `remark` VARCHAR(512) COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_template_code` (`template_code`),
    KEY `idx_biz_type` (`biz_type`),
    KEY `idx_parse_script_id` (`parse_script_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Excel模板配置表';

-- ========================================
-- 9. Excel导入记录表
-- ========================================
DROP TABLE IF EXISTS `excel_import_record`;
CREATE TABLE `excel_import_record` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `import_no` VARCHAR(64) NOT NULL COMMENT '导入批次号',
    `excel_template_id` BIGINT(20) NOT NULL COMMENT 'Excel模板配置ID',
    `file_name` VARCHAR(256) NOT NULL COMMENT '文件名',
    `file_size` BIGINT(20) COMMENT '文件大小（字节）',
    `file_url` VARCHAR(512) COMMENT '文件存储URL',
    `total_rows` INT(11) DEFAULT 0 COMMENT '总行数',
    `success_rows` INT(11) DEFAULT 0 COMMENT '成功行数',
    `fail_rows` INT(11) DEFAULT 0 COMMENT '失败行数',
    `parse_result` TEXT COMMENT '解析结果（JSON格式）',
    `error_details` TEXT COMMENT '错误详情（JSON格式）',
    `import_time` DATETIME NOT NULL COMMENT '导入时间',
    `import_status` TINYINT(1) DEFAULT 0 COMMENT '导入状态：0-处理中, 1-成功, 2-部分成功, 3-失败',
    `cost_time` INT(11) COMMENT '耗时（毫秒）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` VARCHAR(64) COMMENT '创建人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by` VARCHAR(64) COMMENT '更新人',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '删除标记：0-未删除, 1-已删除',
    `remark` VARCHAR(512) COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_import_no` (`import_no`),
    KEY `idx_excel_template_id` (`excel_template_id`),
    KEY `idx_import_time` (`import_time`),
    KEY `idx_import_status` (`import_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Excel导入记录表';

-- ========================================
-- 示例数据：Excel解析脚本
-- ========================================

-- 插入示例解析脚本 - 标准物流模板解析
INSERT INTO `excel_parse_script` (
    `script_code`,
    `script_name`,
    `script_type`,
    `script_content`,
    `description`,
    `ai_generated`,
    `ai_model`
) VALUES (
    'PARSE_STANDARD_LOGISTICS',
    '标准物流模板解析脚本',
    'MVEL',
    'import java.util.*;
import java.math.BigDecimal;

// 解析函数
def parseRow(row) {
    Map result = new HashMap();

    // 解析基本信息
    result.put("waybillNo", row.get(0));  // 运单号
    result.put("weight", new BigDecimal(row.get(1).toString()));  // 重量
    result.put("distance", new BigDecimal(row.get(2).toString()));  // 距离
    result.put("region", row.get(3));  // 地区
    result.put("timeType", row.get(4));  // 时效类型

    // 构造计费因子
    Map factors = new HashMap();
    factors.put("weight", result.get("weight"));
    factors.put("distance", result.get("distance"));
    result.put("factors", factors);

    // 构造计费维度
    Map dimensions = new HashMap();
    dimensions.put("region", result.get("region"));
    dimensions.put("timeType", result.get("timeType"));
    result.put("dimensions", dimensions);

    return result;
}

// 返回解析函数
parseRow;',
    '解析标准物流Excel模板，提取运单号、重量、距离、地区、时效等信息',
    1,
    '通义千问'
);

-- 插入Excel模板配置
INSERT INTO `excel_template_config` (
    `template_code`,
    `template_name`,
    `biz_type`,
    `file_name_pattern`,
    `sheet_name`,
    `header_row`,
    `data_start_row`,
    `parse_script_id`,
    `column_mapping`,
    `priority`,
    `status`
) VALUES (
    'LOGISTICS_STANDARD_TEMPLATE',
    '标准物流计费模板',
    'LOGISTICS_WAYBILL',
    '.*物流.*\\.xlsx?$',
    'Sheet1',
    1,
    2,
    1,
    '[
        {"index": 0, "name": "运单号", "field": "waybillNo", "required": true},
        {"index": 1, "name": "重量(kg)", "field": "weight", "required": true, "type": "NUMERIC"},
        {"index": 2, "name": "距离(km)", "field": "distance", "required": true, "type": "NUMERIC"},
        {"index": 3, "name": "地区", "field": "region", "required": true},
        {"index": 4, "name": "时效类型", "field": "timeType", "required": true}
    ]',
    10,
    1
);
