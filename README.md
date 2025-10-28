# xiupitter计费引擎 - 通用电商物流计费系统

## 项目简介

xiupitter计费引擎是一个高度通用、可配置的计费系统，专为电商和物流行业设计。系统采用微服务架构，支持多种计费模式，可灵活应对不同业务场景的计费需求。

## 核心特性

### 1. 高度通用化
- **多业态支持**：电商订单、物流运单、仓储费用、增值服务等
- **配置化驱动**：所有计费规则都可通过配置实现，无需修改代码
- **维度化管理**：支持多维度的计费条件匹配

### 2. 灵活的计费模式
- **固定值计费**：固定金额
- **线性计费**：基础价格 + 单价 × 数量
- **阶梯计费**：根据区间使用不同单价
- **分段计费**：每个区间独立计算后累加
- **表达式计费**：使用MVEL表达式实现复杂计算
- **查表计费**：根据多维度条件查询价格表

### 3. 强大的表达式引擎
- 基于MVEL表达式引擎
- 支持复杂的业务规则表达
- 支持条件判断、数学运算、逻辑运算

### 4. 完善的功能体系
- **计费要素管理**：定义计费的基础维度和要素
- **计费模板管理**：组织计费规则，支持版本管理
- **计费公式管理**：配置计算逻辑，支持多种计费模式
- **计费执行引擎**：高性能的计费计算引擎
- **计费记录追溯**：完整的计费历史记录

## 技术架构

### 技术栈
- **基础框架**：Spring Boot 2.7.18
- **ORM框架**：MyBatis-Plus 3.5.5
- **数据库**：MySQL 8.0
- **缓存**：Redis + Redisson
- **表达式引擎**：MVEL 2.5.0
- **工具类**：Hutool 5.8.23

### 架构说明

本系统采用**单体应用架构**，各模块作为Maven子模块存在，通过本地依赖直接调用，无需微服务注册中心。

### 模块划分

```
xiupitter-bill-engine
├── billing-common          # 公共模块：基础类、工具类、常量定义
├── billing-api             # API模块：DTO、VO定义
├── billing-factor-service  # 计费要素服务模块
├── billing-template-service# 计费模板服务模块
├── billing-formula-service # 计费公式服务模块
└── billing-engine-service  # 计费引擎服务模块（主启动入口）
```

### 模块依赖关系

```
billing-engine-service (主应用)
    ├── billing-factor-service (本地依赖)
    ├── billing-template-service (本地依赖)
    ├── billing-formula-service (本地依赖)
    ├── billing-api (本地依赖)
    └── billing-common (本地依赖)
```

## 文档导航

- [📊 领域模型关系图](docs/DOMAIN_MODEL.md) - 详细的实体关系和领域模型说明
- [🏗️ 交互式领域模型图](docs/DOMAIN_MODEL.html) - 可在浏览器中查看的领域模型图表
- [📋 系统架构文档](docs/ARCHITECTURE.md) - 技术架构说明
- [📝 Excel解析功能](docs/EXCEL_PARSE.md) - Excel解析功能说明
- [💡 使用示例](docs/USAGE_EXAMPLE.md) - 使用示例和最佳实践

## 核心概念

### 1. 计费要素（Billing Factor）
计费的最小计算单元和维度，定义了参与计费的各种要素。

**示例**：
- 重量（weight）：数值型，单位kg
- 距离（distance）：数值型，单位km
- 地区（region）：枚举型
- 时效类型（timeType）：枚举型

### 2. 计费维度（Billing Dimension）
影响计费的条件维度，用于计费规则的匹配。

**示例**：
- 地区：华东、华南、华北等
- 客户等级：VIP、普通
- 商品类型：普货、冷链、危险品

### 3. 计费公式（Billing Formula）
定义具体的计算逻辑，支持多种计算模式。

**示例公式**：

```java
// 线性计费
"weight * 5.0 + distance * 0.5"

// 阶梯计费配置
[
  {"start":0, "end":10, "unitPrice":5.0, "fixedPrice":0},
  {"start":10, "end":50, "unitPrice":4.0, "fixedPrice":10},
  {"start":50, "end":null, "unitPrice":3.0, "fixedPrice":20}
]

// MVEL表达式
"if (weight > 100) {
    weight * 3.0
} else if (weight > 50) {
    weight * 4.0
} else {
    weight * 5.0
}"
```

### 4. 计费模板（Billing Template）
一组计费规则的容器，定义了特定业务场景下的计费方式。

**特性**：
- 支持维度匹配
- 支持优先级
- 支持生效时间范围
- 支持版本管理

### 5. 计费项目（Billing Item）
具体的收费项，关联计费公式。

**类型**：
- MAIN：主费用（如运费）
- ADDITIONAL：附加费用（如保价费）
- DISCOUNT：折扣
- SURCHARGE：附加费

## 使用示例

### 1. 定义计费要素

```sql
INSERT INTO billing_factor (factor_code, factor_name, factor_type, biz_type, unit, required)
VALUES ('WEIGHT', '重量', 'NUMERIC', 'LOGISTICS_WAYBILL', 'kg', 1);
```

### 2. 配置计费公式

```sql
-- 阶梯计费公式
INSERT INTO billing_formula (
    formula_code,
    formula_name,
    formula_type,
    biz_type,
    input_factors,
    ladder_config
) VALUES (
    'LADDER_WEIGHT_FEE',
    '重量阶梯计费',
    'LADDER',
    'LOGISTICS_WAYBILL',
    '[{"factorCode":"weight","required":true}]',
    '[
        {"start":0,"end":10,"unitPrice":5.0,"fixedPrice":0},
        {"start":10,"end":50,"unitPrice":4.0,"fixedPrice":10},
        {"start":50,"end":null,"unitPrice":3.0,"fixedPrice":20}
    ]'
);
```

### 3. 创建计费模板

```sql
INSERT INTO billing_template (
    template_code,
    template_name,
    biz_type,
    priority,
    status
) VALUES (
    'STANDARD_LOGISTICS',
    '标准物流计费模板',
    'LOGISTICS_WAYBILL',
    10,
    1
);
```

### 4. 关联计费项目

```sql
INSERT INTO billing_item (
    template_id,
    item_code,
    item_name,
    item_type,
    formula_id,
    required
) VALUES (
    1,
    'WEIGHT_FEE',
    '重量费用',
    'MAIN',
    1,
    1
);
```

### 5. 调用计费接口

```json
POST /api/billing/calculate

{
  "bizNo": "ORDER202501130001",
  "bizType": "LOGISTICS_WAYBILL",
  "factors": {
    "weight": 30.5,
    "distance": 500
  },
  "dimensions": {
    "region": "EAST_CHINA",
    "timeType": "STANDARD"
  },
  "dryRun": false
}
```

### 6. 计费结果

```json
{
  "code": 200,
  "message": "计费成功",
  "data": {
    "bizNo": "ORDER202501130001",
    "bizType": "LOGISTICS_WAYBILL",
    "templateCode": "STANDARD_LOGISTICS",
    "templateName": "标准物流计费模板",
    "items": [
      {
        "itemCode": "WEIGHT_FEE",
        "itemName": "重量费用",
        "formulaCode": "LADDER_WEIGHT_FEE",
        "amount": 132.00
      }
    ],
    "totalFee": 132.00,
    "currency": "CNY",
    "billingTime": 1705132800000
  }
}
```

## 业务场景示例

### 场景1：物流运费计算

**需求**：
- 0-10kg：5元/kg
- 10-50kg：4元/kg，首重10元
- 50kg以上：3元/kg，首重20元

**配置**：使用阶梯计费公式

### 场景2：仓储费用计算

**需求**：
- 基础费用：10元/天
- 面积费用：面积 × 0.5元/m²/天
- 温控附加费：如果需要温控，额外加收20元/天

**配置**：
1. 固定值公式：基础费用
2. 线性公式：面积费用
3. 条件公式：温控附加费（带计算条件）

### 场景3：电商订单计费

**需求**：
- 按商品类型不同收费
- VIP客户享受折扣
- 特定地区有附加费

**配置**：
1. 使用维度匹配：地区、客户等级
2. 查表计费：根据商品类型查询基础价格
3. 表达式计费：计算折扣

## 数据库设计

### 核心表结构

1. **billing_factor**：计费要素表
2. **billing_dimension**：计费维度表
3. **billing_formula**：计费公式表
4. **billing_template**：计费模板表
5. **billing_item**：计费项目表
6. **billing_record**：计费记录表

详细建表脚本请查看：`docs/sql/schema.sql`

## 快速开始

### 1. 环境准备

- JDK 1.8+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### 2. 数据库初始化

```bash
# 执行建表脚本
mysql -u root -p < docs/sql/schema.sql
```

### 3. 修改配置

修改 `billing-engine-service/src/main/resources/application.yml` 配置文件：
- 配置数据库连接（url、username、password）
- 配置Redis连接（host、port、password）

### 4. 启动应用

**方式一：使用Maven启动**
```bash
# 进入主模块目录
cd billing-engine-service

# 启动应用
mvn spring-boot:run
```

**方式二：编译打包后启动**
```bash
# 编译整个项目
mvn clean package

# 启动应用（只需启动一个服务）
java -jar billing-engine-service/target/billing-engine-service-1.0.0-SNAPSHOT.jar
```

应用启动后，访问：http://localhost:8080

## 扩展开发

### 添加新的公式类型

1. 在 `FormulaTypeEnum` 中添加新的枚举值
2. 在 `FormulaCalculator` 中实现新的计算方法
3. 更新数据库表注释和文档

### 添加新的业务类型

1. 在 `BizTypeEnum` 中添加新的枚举值
2. 定义该业务类型的计费要素
3. 创建计费模板和公式

## 性能优化

1. **缓存策略**
   - 计费要素、公式、模板使用Redis缓存
   - 使用 `@Cacheable` 注解自动管理缓存

2. **数据库优化**
   - 建立合适的索引
   - 使用连接池（Druid）
   - 读写分离（可选）

3. **计算优化**
   - MVEL表达式编译缓存
   - 批量计费支持
   - 异步计费（使用MQ）

## 系统架构图

```
                    ┌─────────────────────────┐
                    │   HTTP Request (8080)   │
                    └────────────┬────────────┘
                                 │
                    ┌────────────▼────────────┐
                    │  BillingEngineService   │
                    │   (计费引擎核心服务)     │
                    └────────────┬────────────┘
                                 │
        ┌────────────────────────┼────────────────────────┐
        │                        │                        │
┌───────▼──────────┐  ┌──────────▼─────────┐  ┌─────────▼──────────┐
│ BillingTemplate  │  │  BillingFormula    │  │  BillingFactor     │
│    Service       │  │     Service        │  │     Service        │
│  (模板匹配服务)   │  │  (公式计算服务)     │  │  (要素管理服务)     │
└───────┬──────────┘  └──────────┬─────────┘  └─────────┬──────────┘
        │                        │                        │
        └────────────────────────┼────────────────────────┘
                                 │
                ┌────────────────┼────────────────┐
                │                │                │
          ┌─────▼─────┐    ┌────▼────┐    ┌─────▼─────┐
          │   MySQL   │    │  Redis  │    │   MVEL    │
          └───────────┘    └─────────┘    └───────────┘
```

**架构特点**：
- 单一进程启动，所有模块在同一个JVM中运行
- 模块间通过Spring Bean直接注入调用，无网络开销
- 共享同一个数据库连接池和Redis连接
- 统一的事务管理

## 设计模式

1. **策略模式**：不同的计费公式类型使用不同的计算策略
2. **工厂模式**：公式计算器工厂
3. **模板方法模式**：计费流程模板
4. **责任链模式**：模板匹配链

## 作者

xiupitter

## 许可证

MIT License
