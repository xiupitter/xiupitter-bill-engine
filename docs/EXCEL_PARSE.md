# Excel解析功能使用说明

## 一、功能概述

Excel解析功能是计费引擎系统的重要组成部分，用于动态解析不同格式的Excel计费模板文件。核心特性包括：

1. **动态脚本解析**：使用MVEL脚本动态解析Excel数据
2. **AI生成脚本**：支持通过AI（如通义千问）生成解析脚本
3. **模板自动匹配**：根据文件名自动匹配对应的解析模板
4. **脚本版本管理**：支持脚本版本控制和更新
5. **解析结果追溯**：完整记录每次导入的解析结果

## 二、数据模型

### 1. Excel解析脚本表 (excel_parse_script)

存储MVEL解析脚本，用于解析Excel数据行。

**核心字段**：
- `script_code`: 脚本编码（唯一）
- `script_name`: 脚本名称
- `script_type`: 脚本类型（MVEL/GROOVY/JAVASCRIPT）
- `script_content`: MVEL脚本内容
- `ai_generated`: 是否AI生成
- `ai_model`: AI模型名称

### 2. Excel模板配置表 (excel_template_config)

定义Excel模板的结构和解析规则。

**核心字段**：
- `template_code`: 模板编码
- `biz_type`: 业务类型
- `file_name_pattern`: 文件名匹配正则表达式
- `sheet_name`: Sheet名称
- `header_row`: 表头行号
- `data_start_row`: 数据起始行号
- `parse_script_id`: 关联的解析脚本ID
- `column_mapping`: 列映射配置（JSON）

### 3. Excel导入记录表 (excel_import_record)

记录每次Excel导入的结果。

**核心字段**：
- `import_no`: 导入批次号
- `total_rows`: 总行数
- `success_rows`: 成功行数
- `fail_rows`: 失败行数
- `parse_result`: 解析结果（JSON）
- `error_details`: 错误详情（JSON）

## 三、使用流程

### 1. 创建解析脚本

使用AI（如通义千问）生成MVEL解析脚本：

**提示词示例**：
```
请生成一个MVEL脚本，用于解析物流运单Excel数据，Excel有5列：
列0：运单号
列1：重量(kg)
列2：距离(km)
列3：地区
列4：时效类型

脚本需要返回一个解析函数，输入是Map<Integer, Object>的行数据，
输出是Map<String, Object>包含：
- waybillNo: 运单号
- factors: 计费因子(weight, distance)
- dimensions: 计费维度(region, timeType)
```

**API接口**：
```http
POST /api/excel/script
Content-Type: application/json

{
  "scriptCode": "PARSE_STANDARD_LOGISTICS",
  "scriptName": "标准物流模板解析脚本",
  "scriptType": "MVEL",
  "scriptContent": "...",
  "description": "解析标准物流Excel模板",
  "aiGenerated": 1,
  "aiModel": "通义千问"
}
```

### 2. 创建Excel模板配置

配置Excel模板的结构和关联的解析脚本：

```http
POST /api/excel/template
Content-Type: application/json

{
  "templateCode": "LOGISTICS_STANDARD_TEMPLATE",
  "templateName": "标准物流计费模板",
  "bizType": "LOGISTICS_WAYBILL",
  "fileNamePattern": ".*物流.*\\.xlsx?$",
  "sheetName": "Sheet1",
  "headerRow": 1,
  "dataStartRow": 2,
  "parseScriptId": 1,
  "columnMapping": "[{\"index\":0,\"name\":\"运单号\",\"field\":\"waybillNo\"}]",
  "priority": 10,
  "status": 1
}
```

### 3. 上传并解析Excel

```http
POST /api/excel/parse/upload
Content-Type: multipart/form-data

file: [Excel文件]
bizType: LOGISTICS_WAYBILL (可选)
```

**响应示例**：
```json
{
  "code": 200,
  "message": "Excel解析成功",
  "data": {
    "importNo": "IMP17051328000001234",
    "templateCode": "LOGISTICS_STANDARD_TEMPLATE",
    "templateName": "标准物流计费模板",
    "totalRows": 100,
    "successRows": 98,
    "failRows": 2,
    "parsedData": [
      {
        "waybillNo": "WB001",
        "factors": {
          "weight": 30.5,
          "distance": 500
        },
        "dimensions": {
          "region": "EAST_CHINA",
          "timeType": "STANDARD"
        },
        "_rowIndex": 2
      }
    ],
    "errors": [
      {
        "rowIndex": 15,
        "error": "重量格式错误"
      }
    ],
    "costTime": 1250
  }
}
```

## 四、MVEL脚本编写指南

### 脚本结构

解析脚本应返回一个解析函数，该函数接收一行数据并返回解析结果：

```java
import java.util.*;
import java.math.BigDecimal;

// 定义解析函数
def parseRow(row) {
    Map result = new HashMap();

    // 1. 提取基本字段
    result.put("waybillNo", row.get(0));
    result.put("weight", new BigDecimal(row.get(1).toString()));
    result.put("distance", new BigDecimal(row.get(2).toString()));

    // 2. 构造计费因子
    Map factors = new HashMap();
    factors.put("weight", result.get("weight"));
    factors.put("distance", result.get("distance"));
    result.put("factors", factors);

    // 3. 构造计费维度
    Map dimensions = new HashMap();
    dimensions.put("region", row.get(3));
    dimensions.put("timeType", row.get(4));
    result.put("dimensions", dimensions);

    return result;
}

// 返回解析函数
parseRow;
```

### 常用技巧

#### 1. 数据类型转换
```java
// 转换为BigDecimal
new BigDecimal(row.get(1).toString())

// 转换为Integer
Integer.parseInt(row.get(2).toString())

// 转换为String
row.get(3).toString()
```

#### 2. 条件判断
```java
// 根据条件设置不同的值
if (row.get(1) != null && !row.get(1).toString().isEmpty()) {
    result.put("weight", new BigDecimal(row.get(1).toString()));
} else {
    result.put("weight", BigDecimal.ZERO);
}
```

#### 3. 数据验证
```java
// 验证必填字段
if (row.get(0) == null || row.get(0).toString().isEmpty()) {
    throw new RuntimeException("运单号不能为空");
}
```

## 五、API接口文档

### 1. 脚本管理接口

#### 查询脚本列表
```
GET /api/excel/script/list?current=1&size=10&scriptName=物流&status=1
```

#### 查询脚本详情
```
GET /api/excel/script/{id}
```

#### 创建脚本
```
POST /api/excel/script
```

#### 更新脚本
```
PUT /api/excel/script
```

#### 删除脚本
```
DELETE /api/excel/script/{id}
```

#### 测试脚本
```
POST /api/excel/script/test?scriptId=1
Body: {"row": [0: "WB001", 1: "30.5", 2: "500", 3: "EAST_CHINA", 4: "STANDARD"]}
```

#### 启用/禁用脚本
```
PUT /api/excel/script/{id}/status?status=1
```

### 2. 模板配置接口

#### 查询模板列表
```
GET /api/excel/template/list?current=1&size=10
```

#### 查询模板详情
```
GET /api/excel/template/{id}
```

#### 创建模板
```
POST /api/excel/template
```

#### 更新模板
```
PUT /api/excel/template
```

#### 删除模板
```
DELETE /api/excel/template/{id}
```

#### 匹配模板
```
GET /api/excel/template/match?fileName=物流运单.xlsx&bizType=LOGISTICS_WAYBILL
```

### 3. Excel解析接口

#### 上传并解析Excel
```
POST /api/excel/parse/upload
Content-Type: multipart/form-data
```

#### 查询导入记录
```
GET /api/excel/parse/record/{importNo}
```

## 六、最佳实践

### 1. 脚本编写建议

- **使用AI辅助生成**：通过通义千问等AI工具生成初始脚本，然后根据实际需求调整
- **充分测试**：使用测试接口验证脚本在各种数据情况下的表现
- **错误处理**：脚本中加入适当的异常处理和验证逻辑
- **性能优化**：避免在脚本中执行复杂的计算或外部调用

### 2. 模板配置建议

- **文件名模式**：使用精确的正则表达式，避免匹配到错误的文件
- **优先级设置**：为不同的模板设置合理的优先级，确保正确匹配
- **列映射配置**：详细配置列映射信息，便于后续维护

### 3. 系统扩展

#### 添加新的业务类型

1. 在 `BizTypeEnum` 中添加新的业务类型
2. 创建对应的解析脚本
3. 创建Excel模板配置
4. 测试并验证

#### 支持新的脚本类型

系统预留了对Groovy和JavaScript的支持，可通过扩展 `ScriptEngine` 类实现。

## 七、故障排查

### 常见问题

#### 1. 脚本执行失败

**原因**：
- MVEL语法错误
- 数据类型转换失败
- 空指针异常

**解决方法**：
- 使用测试接口验证脚本
- 检查数据格式是否符合预期
- 添加空值判断

#### 2. 模板匹配失败

**原因**：
- 文件名正则表达式不正确
- 模板未启用
- 优先级设置不当

**解决方法**：
- 使用匹配接口测试文件名
- 检查模板状态
- 调整优先级

#### 3. 解析性能问题

**原因**：
- 数据量过大
- 脚本执行效率低
- 数据库写入慢

**解决方法**：
- 使用批量导入
- 优化MVEL脚本
- 使用异步处理

## 八、安全注意事项

1. **脚本安全**：
   - 限制脚本中可以调用的类和方法
   - 避免执行系统命令
   - 设置脚本执行超时

2. **文件上传安全**：
   - 限制文件大小
   - 验证文件类型
   - 防止路径遍历攻击

3. **权限控制**：
   - 脚本管理需要管理员权限
   - 模板配置需要审核
   - 导入记录需要访问控制

## 九、示例场景

### 场景1：标准物流运单解析

**Excel格式**：
```
| 运单号 | 重量(kg) | 距离(km) | 地区      | 时效类型 |
|--------|----------|----------|-----------|----------|
| WB001  | 30.5     | 500      | EAST_CHINA| STANDARD |
| WB002  | 50.0     | 800      | SOUTH     | EXPRESS  |
```

**解析脚本**：已在数据库示例数据中提供

**使用方式**：
1. 上传文件名包含"物流"的Excel文件
2. 系统自动匹配 `LOGISTICS_STANDARD_TEMPLATE` 模板
3. 使用 `PARSE_STANDARD_LOGISTICS` 脚本解析
4. 返回结构化的计费数据

### 场景2：电商订单解析

**需求**：解析电商订单Excel，提取订单号、商品信息、收货地址等

**实现步骤**：
1. 使用AI生成电商订单解析脚本
2. 创建电商订单模板配置
3. 上传订单Excel文件
4. 获取解析结果并进行计费

## 十、总结

Excel解析功能通过MVEL脚本引擎和AI辅助，实现了高度灵活和可扩展的Excel数据解析能力。主要优势包括：

1. **零代码配置**：通过脚本和配置实现Excel解析，无需修改代码
2. **AI赋能**：利用AI生成解析脚本，降低开发成本
3. **高度灵活**：支持任意格式的Excel模板
4. **易于维护**：脚本版本管理，配置化模板映射
5. **生产可用**：完整的错误处理、日志记录和结果追溯
