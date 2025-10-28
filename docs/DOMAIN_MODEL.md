# 物流计费系统 - 领域模型关系图

## 领域模型概述

本系统是一个灵活的物流计费引擎，支持多种计费模式、阶梯计价、动态规则匹配等功能。

## 核心实体关系图

```mermaid
erDiagram
    BaseEntity ||--o{ BillingTemplate : "extends"
    BaseEntity ||--o{ BillingItem : "extends"
    BaseEntity ||--o{ BillingFormula : "extends"
    BaseEntity ||--o{ BillingFactor : "extends"
    BaseEntity ||--o{ BillingDimension : "extends"
    BaseEntity ||--o{ BillingRecord : "extends"
    
    BillingTemplate ||--o{ BillingItem : "包含"
    BillingItem }o--|| BillingFormula : "使用"
    BillingFactor ||--o{ BillingFormula : "作为输入"
    BillingDimension ||--o{ BillingTemplate : "匹配条件"
    BillingRecord }o--|| BillingTemplate : "使用"
    
    BizTypeEnum ||--o{ BillingTemplate : "分类"
    BizTypeEnum ||--o{ BillingFormula : "适用"
    BizTypeEnum ||--o{ BillingFactor : "适用"
    BizTypeEnum ||--o{ BillingDimension : "适用"
    
    FactorTypeEnum ||--o{ BillingFactor : "类型"
    FormulaTypeEnum ||--o{ BillingFormula : "类型"
    
    BillingTemplate {
        Long id PK
        String templateCode UK "模板编码"
        String templateName "模板名称"
        String bizType "业务类型"
        String description "描述"
        String dimensionConfig "维度配置JSON"
        Integer priority "优先级"
        LocalDateTime effectiveStartTime "生效开始时间"
        LocalDateTime effectiveEndTime "生效结束时间"
        Integer version "版本号"
        Integer isDefault "是否默认"
        Integer status "状态"
        LocalDateTime createTime
        String createBy
        LocalDateTime updateTime
        String updateBy
        Integer deleted
        String remark
    }
    
    BillingItem {
        Long id PK
        Long templateId FK "所属模板"
        String itemCode UK "项目编码"
        String itemName "项目名称"
        String itemType "项目类型(主费用/附加费用/折扣/附加费)"
        Long formulaId FK "计费公式"
        Integer required "是否必须计算"
        BigDecimal minValue "最小值"
        BigDecimal maxValue "最大值"
        String calculateCondition "计算条件表达式"
        Integer sortOrder "排序"
        Integer status "状态"
        LocalDateTime createTime
        String createBy
        LocalDateTime updateTime
        String updateBy
        Integer deleted
        String remark
    }
    
    BillingFormula {
        Long id PK
        String formulaCode UK "公式编码"
        String formulaName "公式名称"
        String formulaType "公式类型(固定值/线性/阶梯/分段/表达式/查表)"
        String bizType "业务类型"
        String description "描述"
        String formulaExpression "公式表达式"
        String inputFactors "输入要素JSON"
        String ladderConfig "阶梯配置JSON"
        String priceTableConfig "价格表配置JSON"
        String resultUnit "结果单位"
        Integer precision "精度"
        String roundingMode "舍入模式"
        Integer version "版本号"
        Integer status "状态"
        LocalDateTime createTime
        String createBy
        LocalDateTime updateTime
        String updateBy
        Integer deleted
        String remark
    }
    
    BillingFactor {
        Long id PK
        String factorCode UK "要素编码"
        String factorName "要素名称"
        String factorType "要素类型(数值/枚举/日期/布尔/文本)"
        String bizType "业务类型"
        String unit "数据单位"
        String defaultValue "默认值"
        Integer required "是否必填"
        String validationRule "验证规则"
        String enumConfig "枚举值配置JSON"
        Integer sortOrder "排序"
        Integer status "状态"
        LocalDateTime createTime
        String createBy
        LocalDateTime updateTime
        String updateBy
        Integer deleted
        String remark
    }
    
    BillingDimension {
        Long id PK
        String dimensionCode UK "维度编码"
        String dimensionName "维度名称"
        String bizType "业务类型"
        Long parentId FK "父维度ID"
        Integer level "层级"
        String dimensionValues "维度值配置JSON"
        Integer multiSelect "是否多选"
        Integer sortOrder "排序"
        Integer status "状态"
        LocalDateTime createTime
        String createBy
        LocalDateTime updateTime
        String updateBy
        Integer deleted
        String remark
    }
    
    BillingRecord {
        Long id PK
        String bizNo "业务单号"
        String bizType "业务类型"
        String templateCode "使用的模板编码"
        String factors "计费要素JSON"
        String dimensions "计费维度JSON"
        BigDecimal totalFee "总费用"
        String currency "币种"
        String itemDetails "计费项明细JSON"
        LocalDateTime billingTime "计费时间"
        Integer status "状态"
        String failReason "失败原因"
        LocalDateTime createTime
        String createBy
        LocalDateTime updateTime
        String updateBy
        Integer deleted
        String remark
    }
    
    BizTypeEnum {
        String code PK
        String desc
    }
    
    FactorTypeEnum {
        String code PK
        String desc
    }
    
    FormulaTypeEnum {
        String code PK
        String desc
    }
```

## 领域模型说明

### 核心概念

1. **BillingTemplate（计费模板）**
   - 定义特定业务场景下的计费规则集合
   - 通过维度配置支持条件匹配
   - 示例：标准物流计费模板、电商订单计费模板、冷链运输计费模板

2. **BillingItem（计费项目）**
   - 模板下的具体收费项
   - 每个项目关联一个计费公式
   - 支持计算条件控制是否计算
   - 示例：运费、保价费、包装费、仓储费

3. **BillingFormula（计费公式）**
   - 定义具体的计算逻辑
   - 支持多种计算模式：
     - 固定值
     - 线性计算（基础价格 + 单价 × 数量）
     - 阶梯计算（根据区间不同单价）
     - 分段计算（每个区间独立计算）
     - 表达式计算（MVEL表达式）
     - 查表计算（价格表查询）

4. **BillingFactor（计费要素）**
   - 参与计费的最小计算单元
   - 定义了各种维度的数据类型
   - 示例：重量、距离、体积、时效、地区

5. **BillingDimension（计费维度）**
   - 用于计费规则匹配的条件维度
   - 支持层级结构
   - 示例：地区、客户等级、商品类型、时段

6. **BillingRecord（计费记录）**
   - 记录每次计费的结果
   - 保存计费要素、维度、明细等信息
   - 用于审计和追溯

### 核心关系

```
BillingTemplate 1:N BillingItem
├─ BillingItem N:1 BillingFormula
│  └─ BillingFormula 使用 BillingFactor 作为输入
└─ BillingTemplate 通过 BillingDimension 进行匹配

计费流程：
业务单据 → 提取计费要素和维度
  → 匹配 BillingTemplate
  → 遍历 BillingItem
  → 使用 BillingFormula 计算
  → 生成 BillingRecord
```

## 枚举类型

### BizTypeEnum（业务类型）
- E_COMMERCE_ORDER（电商订单）
- LOGISTICS_WAYBILL（物流运单）
- WAREHOUSE_FEE（仓储费用）
- VALUE_ADDED_SERVICE（增值服务）

### FactorTypeEnum（要素类型）
- NUMERIC（数值型）
- ENUM（枚举型）
- DATE（日期型）
- BOOLEAN（布尔型）
- TEXT（文本型）

### FormulaTypeEnum（公式类型）
- FIXED（固定值）
- LINEAR（线性计算）
- LADDER（阶梯计算）
- SEGMENT（分段计算）
- EXPRESSION（表达式计算）
- TABLE_LOOKUP（查表计算）

## 领域模型特点

1. **高度可配置**：支持动态配置计费规则，无需修改代码
2. **灵活的计算模式**：支持多种计算方式，适应复杂计费场景
3. **维度匹配**：通过维度条件自动匹配最合适的计费模板
4. **版本管理**：模板和公式支持版本控制
5. **审计追溯**：完整记录计费过程和结果
6. **可扩展性**：通过业务类型和要素类型支持业务扩展

## 典型场景示例

### 场景1：标准物流运费计算
```
模板：标准物流运费模板
维度：{region: "华北地区", customerLevel: "普通"}
项目：
  - 基础运费（固定值：10元）
  - 重量运费（线性：单价 * 重量）
  - 距离运费（阶梯：按距离区间计算）
要素：{weight: 50kg, distance: 200km}
```

### 场景2：冷链运输计费
```
模板：冷链运输计费模板
维度：{productType: "冷链", temperature: "冷冻"}
项目：
  - 基础运费
  - 温控费用（根据温度等级）
  - 时效加急费
要素：{weight: 100kg, distance: 500km, temperature: "-18℃", urgency: "次日达"}
```
