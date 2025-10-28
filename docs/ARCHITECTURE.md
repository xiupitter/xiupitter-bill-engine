# 计费引擎架构说明

## 一、架构选型：单体应用

本系统采用**单体应用架构**（Monolithic Architecture），而非微服务架构。

### 为什么选择单体架构？

1. **简化部署**：只需要启动一个应用进程，无需管理多个服务
2. **降低运维成本**：不需要额外的服务注册中心、配置中心、API网关
3. **性能更高**：模块间直接方法调用，无网络传输开销
4. **事务管理简单**：在同一个JVM中，事务管理更容易
5. **开发调试方便**：无需同时启动多个服务，降低开发复杂度

### 什么时候考虑微服务？

当系统面临以下情况时，可以考虑拆分为微服务：
- 团队规模扩大，需要多个团队独立开发部署
- 某个模块需要独立扩展（如公式计算模块需要更多计算资源）
- 不同模块使用不同技术栈
- 业务复杂度急剧增加

## 二、模块职责

### 1. billing-common（公共模块）
**职责**：提供所有模块共用的基础设施

**主要内容**：
- BaseEntity：实体基类
- Result：统一响应结果
- 枚举类：FactorTypeEnum、FormulaTypeEnum、BizTypeEnum
- 工具类

**依赖**：无其他模块依赖

### 2. billing-api（API模块）
**职责**：定义DTO和VO，作为数据传输对象

**主要内容**：
- BillingRequest：计费请求DTO
- BillingResult：计费结果DTO
- BillingTemplateDTO：模板详情DTO

**依赖**：billing-common

### 3. billing-factor-service（计费要素模块）
**职责**：管理计费要素和维度

**主要内容**：
- BillingFactor：计费要素实体
- BillingDimension：计费维度实体
- BillingFactorService：要素管理服务
- BillingFactorController：要素管理接口

**依赖**：billing-common、billing-api

**对外提供**：
- 要素的增删改查
- 根据业务类型查询要素列表

### 4. billing-formula-service（计费公式模块）
**职责**：管理计费公式，执行公式计算

**主要内容**：
- BillingFormula：计费公式实体
- FormulaCalculator：公式计算引擎（核心）
- BillingFormulaService：公式管理服务
- BillingFormulaController：公式管理接口

**依赖**：billing-common、billing-api、MVEL表达式引擎

**对外提供**：
- 公式的增删改查
- 执行公式计算：`calculate(formulaId, context)`

**核心算法**：
- 固定值计算
- 线性计算
- 阶梯计算
- 分段计算
- MVEL表达式计算
- 查表计算

### 5. billing-template-service（计费模板模块）
**职责**：管理计费模板，匹配最合适的模板

**主要内容**：
- BillingTemplate：计费模板实体
- BillingItem：计费项目实体
- TemplateMatchEngine：模板匹配引擎（核心）
- BillingTemplateService：模板管理服务
- BillingTemplateController：模板管理接口

**依赖**：billing-common、billing-api

**对外提供**：
- 模板的增删改查
- 智能模板匹配：`matchTemplate(bizType, dimensions)`
- 查询模板详情（带计费项列表）

**核心算法**：
- 根据业务类型过滤
- 根据生效时间过滤
- 根据维度条件匹配（支持精确匹配、数组匹配、范围匹配）
- 按匹配度和优先级排序

### 6. billing-engine-service（计费引擎模块）
**职责**：核心计费执行引擎，协调各个模块完成计费

**主要内容**：
- BillingRecord：计费记录实体
- BillingEngineService：计费引擎服务（核心）
- BillingEngineController：计费接口
- BillingEngineApplication：应用启动类

**依赖**：billing-common、billing-api、billing-factor-service、billing-template-service、billing-formula-service

**对外提供**：
- 计费接口：`POST /api/billing/calculate`
- 查询计费记录：`GET /api/billing/record/{bizNo}`

**核心流程**：
```
1. 接收计费请求
   ↓
2. 模板匹配（调用 BillingTemplateService）
   ↓
3. 要素校验
   ↓
4. 遍历计费项
   ├─ 判断计算条件（MVEL表达式）
   └─ 调用 BillingFormulaService 执行计算
   ↓
5. 汇总计费结果
   ↓
6. 保存计费记录（非试算模式）
   ↓
7. 返回计费结果
```

## 三、调用链路

### 场景：计算物流运费

```
[用户请求]
    ↓
POST /api/billing/calculate
{
  "bizNo": "ORDER_001",
  "bizType": "LOGISTICS_WAYBILL",
  "factors": {"weight": 30.5},
  "dimensions": {"region": "EAST_CHINA"}
}
    ↓
[BillingEngineController]
    ↓
[BillingEngineService.billing()]
    ↓
1. 调用 billingTemplateService.matchTemplate()
   └─ [TemplateMatchEngine] 执行模板匹配算法
      └─ 返回: STANDARD_LOGISTICS模板 + 计费项列表
    ↓
2. 遍历计费项:

   计费项1: BASE_FEE (基础运费)
   ├─ 调用 billingFormulaService.calculate(formulaId=1, {"weight": 30.5})
   │  └─ [FormulaCalculator] 执行固定值计算
   │     └─ 返回: 10.00元

   计费项2: WEIGHT_FEE (重量费用)
   ├─ 调用 billingFormulaService.calculate(formulaId=2, {"weight": 30.5})
   │  └─ [FormulaCalculator] 执行阶梯计算
   │     ├─ 判断30.5kg在哪个阶梯: 10-50kg
   │     ├─ 应用单价: 4元/kg + 首重10元
   │     └─ 返回: 132.00元
    ↓
3. 汇总结果: 10.00 + 132.00 = 142.00元
    ↓
4. 保存计费记录到 billing_record 表
    ↓
5. 返回计费结果
```

## 四、数据流转

### 1. 计费要素流转
```
用户配置要素 → billing_factor表
                    ↓
模板配置时引用要素 → billing_formula.input_factors
                    ↓
计费时传入要素值 → BillingRequest.factors
                    ↓
公式计算器使用 → FormulaCalculator.calculate(context)
```

### 2. 维度条件流转
```
用户配置维度 → billing_dimension表
                    ↓
模板配置时设置维度条件 → billing_template.dimension_config
                    ↓
计费时传入维度值 → BillingRequest.dimensions
                    ↓
模板匹配器使用 → TemplateMatchEngine.matchTemplate()
```

## 五、关键设计

### 1. 模板匹配算法

**输入**：业务类型 + 维度条件
**输出**：最合适的计费模板

**算法步骤**：
```java
1. 查询该业务类型下所有启用的模板
2. 过滤：只保留当前时间在生效期内的模板
3. 匹配：根据维度条件计算每个模板的匹配分数
   - 完全匹配所有维度：匹配分数 = 维度数量
   - 部分匹配：匹配分数 = 0（不采用）
4. 排序：
   - 第一优先级：匹配分数从高到低
   - 第二优先级：模板优先级从高到低
5. 返回：排序后的第一个模板
6. 兜底：如果没有匹配到，返回默认模板
```

### 2. 公式计算引擎

**核心接口**：
```java
BigDecimal calculate(BillingFormula formula, Map<String, Object> context)
```

**策略模式实现**：
```java
switch (formulaType) {
    case FIXED: return calculateFixed();
    case LINEAR: return calculateLinear();
    case LADDER: return calculateLadder();
    case SEGMENT: return calculateSegment();
    case EXPRESSION: return calculateExpression();
    case TABLE_LOOKUP: return calculateTableLookup();
}
```

### 3. 条件计算

计费项支持设置`calculateCondition`字段，使用MVEL表达式判断是否需要计算：

```java
// 只有重量大于100kg时才收取大件费
"weight > 100"

// 偏远地区且重量大于50kg才收附加费
"region == 'REMOTE' && weight > 50"
```

## 六、扩展性设计

### 1. 新增计费公式类型

只需要：
1. 在`FormulaTypeEnum`中添加新枚举
2. 在`FormulaCalculator`中实现新的计算方法

```java
case NEW_TYPE:
    return calculateNewType(formula, context);
```

### 2. 新增业务类型

只需要：
1. 在`BizTypeEnum`中添加新枚举
2. 配置该业务类型的要素、公式、模板

### 3. 迁移到微服务

如果未来需要拆分为微服务，只需要：
1. 恢复Feign客户端接口
2. 修改`BillingEngineService`注入Feign客户端而不是Service
3. 为各服务添加Nacos配置
4. 独立部署各个服务

## 七、性能优化

### 1. 缓存策略

- **要素缓存**：`billing:factor:{factorCode}`
- **公式缓存**：`billing:formula:{formulaId}`
- **模板缓存**：`billing:template:detail:{templateCode}`

### 2. 数据库优化

- 为所有状态字段建索引
- 为业务类型字段建索引
- 为模板优先级字段建索引

### 3. 计算优化

- MVEL表达式编译缓存
- 连接池配置优化
- 事务范围最小化

## 八、总结

本系统采用单体应用架构，通过合理的模块划分和清晰的职责边界，实现了高内聚低耦合。各模块通过Spring Bean直接注入调用，避免了网络开销，同时保持了良好的可维护性和可扩展性。

当业务规模增长到一定程度，可以平滑地迁移到微服务架构，而无需大规模重构代码。
