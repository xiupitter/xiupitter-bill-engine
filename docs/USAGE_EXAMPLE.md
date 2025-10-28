# 计费引擎使用示例

## 一、完整业务场景示例

### 场景：物流运费计费

**业务需求**：
- 基础运费：固定10元
- 重量费用：阶梯计费
  - 0-10kg：5元/kg
  - 10-50kg：4元/kg，首重10元
  - 50kg以上：3元/kg，首重20元

## 二、配置步骤

### 1. 定义计费要素

```sql
-- 插入重量要素
INSERT INTO billing_factor (factor_code, factor_name, factor_type, biz_type, unit, required, status)
VALUES ('WEIGHT', '重量', 'NUMERIC', 'LOGISTICS_WAYBILL', 'kg', 1, 1);

-- 插入距离要素
INSERT INTO billing_factor (factor_code, factor_name, factor_type, biz_type, unit, required, status)
VALUES ('DISTANCE', '距离', 'NUMERIC', 'LOGISTICS_WAYBILL', 'km', 1, 1);
```

### 2. 创建计费公式

```sql
-- 固定基础运费公式
INSERT INTO billing_formula (
    formula_code,
    formula_name,
    formula_type,
    biz_type,
    description,
    formula_expression,
    precision,
    status
) VALUES (
    'FIXED_BASE_FEE',
    '固定基础运费',
    'FIXED',
    'LOGISTICS_WAYBILL',
    '固定10元基础运费',
    '10.00',
    2,
    1
);

-- 重量阶梯计费公式
INSERT INTO billing_formula (
    formula_code,
    formula_name,
    formula_type,
    biz_type,
    description,
    input_factors,
    ladder_config,
    precision,
    status
) VALUES (
    'LADDER_WEIGHT_FEE',
    '重量阶梯计费',
    'LADDER',
    'LOGISTICS_WAYBILL',
    '0-10kg:5元/kg, 10-50kg:4元/kg, >50kg:3元/kg',
    '[{"factorCode":"weight","required":true}]',
    '[
        {"start":0,"end":10,"unitPrice":5.0,"fixedPrice":0},
        {"start":10,"end":50,"unitPrice":4.0,"fixedPrice":10},
        {"start":50,"end":null,"unitPrice":3.0,"fixedPrice":20}
    ]',
    2,
    1
);
```

### 3. 创建计费模板

```sql
-- 创建标准物流计费模板
INSERT INTO billing_template (
    template_code,
    template_name,
    biz_type,
    description,
    priority,
    is_default,
    status
) VALUES (
    'STANDARD_LOGISTICS',
    '标准物流计费模板',
    'LOGISTICS_WAYBILL',
    '适用于普通物流运单',
    10,
    1,
    1
);
```

### 4. 配置计费项

```sql
-- 获取模板ID和公式ID（假设模板ID=1，公式ID分别为1和2）

-- 基础运费项
INSERT INTO billing_item (
    template_id,
    item_code,
    item_name,
    item_type,
    formula_id,
    required,
    sort_order,
    status
) VALUES (
    1,
    'BASE_FEE',
    '基础运费',
    'MAIN',
    1,
    1,
    1,
    1
);

-- 重量费用项
INSERT INTO billing_item (
    template_id,
    item_code,
    item_name,
    item_type,
    formula_id,
    required,
    sort_order,
    status
) VALUES (
    1,
    'WEIGHT_FEE',
    '重量费用',
    'ADDITIONAL',
    2,
    1,
    2,
    1
);
```

## 三、调用计费接口

### 1. 试算模式（不保存记录）

**请求示例**：

```bash
curl -X POST http://localhost:8084/api/billing/calculate \
  -H "Content-Type: application/json" \
  -d '{
    "bizNo": "TEST_ORDER_001",
    "bizType": "LOGISTICS_WAYBILL",
    "factors": {
      "weight": 30.5
    },
    "dryRun": true
  }'
```

**响应示例**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "bizNo": "TEST_ORDER_001",
    "bizType": "LOGISTICS_WAYBILL",
    "templateCode": "STANDARD_LOGISTICS",
    "templateName": "标准物流计费模板",
    "items": [
      {
        "itemCode": "BASE_FEE",
        "itemName": "基础运费",
        "itemType": "MAIN",
        "formulaCode": null,
        "formulaName": null,
        "amount": 10.00,
        "inputParams": {
          "weight": 30.5
        }
      },
      {
        "itemCode": "WEIGHT_FEE",
        "itemName": "重量费用",
        "itemType": "ADDITIONAL",
        "formulaCode": null,
        "formulaName": null,
        "amount": 132.00,
        "inputParams": {
          "weight": 30.5
        }
      }
    ],
    "totalFee": 142.00,
    "currency": "CNY",
    "billingTime": 1705132800000,
    "dryRun": true,
    "billingRecordId": null
  }
}
```

**计算说明**：
- 基础运费：10元（固定）
- 重量费用：30.5kg在10-50kg区间，使用单价4元/kg + 首重10元 = 30.5 × 4 + 10 = 132元
- 总费用：10 + 132 = 142元

### 2. 正式计费（保存记录）

**请求示例**：

```bash
curl -X POST http://localhost:8084/api/billing/calculate \
  -H "Content-Type: application/json" \
  -d '{
    "bizNo": "ORDER_20250113_001",
    "bizType": "LOGISTICS_WAYBILL",
    "factors": {
      "weight": 60.0
    },
    "dryRun": false
  }'
```

**响应示例**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "bizNo": "ORDER_20250113_001",
    "bizType": "LOGISTICS_WAYBILL",
    "templateCode": "STANDARD_LOGISTICS",
    "templateName": "标准物流计费模板",
    "items": [
      {
        "itemCode": "BASE_FEE",
        "itemName": "基础运费",
        "itemType": "MAIN",
        "amount": 10.00
      },
      {
        "itemCode": "WEIGHT_FEE",
        "itemName": "重量费用",
        "itemType": "ADDITIONAL",
        "amount": 200.00
      }
    ],
    "totalFee": 210.00,
    "currency": "CNY",
    "billingTime": 1705132800000,
    "dryRun": false,
    "billingRecordId": 1234567890
  }
}
```

**计算说明**：
- 基础运费：10元（固定）
- 重量费用：60kg超过50kg，使用单价3元/kg + 首重20元 = 60 × 3 + 20 = 200元
- 总费用：10 + 200 = 210元

### 3. 查询计费记录

```bash
curl http://localhost:8084/api/billing/record/ORDER_20250113_001
```

## 四、高级场景示例

### 场景1：分段计费（每段独立计算）

如果使用分段计费而不是阶梯计费，60kg的计费方式为：
- 0-10kg：10kg × 5元/kg = 50元
- 10-50kg：40kg × 4元/kg = 160元
- 50-60kg：10kg × 3元/kg = 30元
- 总计：50 + 160 + 30 = 240元

**公式配置**：

```sql
INSERT INTO billing_formula (
    formula_code,
    formula_name,
    formula_type,
    biz_type,
    ladder_config,
    status
) VALUES (
    'SEGMENT_WEIGHT_FEE',
    '重量分段计费',
    'SEGMENT',
    'LOGISTICS_WAYBILL',
    '[
        {"start":0,"end":10,"unitPrice":5.0,"fixedPrice":0},
        {"start":10,"end":50,"unitPrice":4.0,"fixedPrice":0},
        {"start":50,"end":null,"unitPrice":3.0,"fixedPrice":0}
    ]',
    1
);
```

### 场景2：条件计费（带计算条件）

只有当重量大于100kg时才收取大件费：

```sql
INSERT INTO billing_item (
    template_id,
    item_code,
    item_name,
    item_type,
    formula_id,
    calculate_condition,
    status
) VALUES (
    1,
    'HEAVY_SURCHARGE',
    '大件附加费',
    'SURCHARGE',
    3,
    'weight > 100',
    1
);
```

### 场景3：复杂表达式计费

使用MVEL表达式实现复杂计费逻辑：

```sql
INSERT INTO billing_formula (
    formula_code,
    formula_name,
    formula_type,
    biz_type,
    formula_expression,
    status
) VALUES (
    'COMPLEX_EXPRESSION',
    '复杂表达式计费',
    'EXPRESSION',
    'LOGISTICS_WAYBILL',
    'if (weight > 100) {
        weight * 3.0 * 0.9
     } else if (weight > 50) {
        weight * 4.0 * 0.95
     } else {
        weight * 5.0
     }',
    1
);
```

### 场景4：维度匹配（不同地区不同价格）

华东地区价格模板：

```sql
INSERT INTO billing_template (
    template_code,
    template_name,
    biz_type,
    dimension_config,
    priority,
    status
) VALUES (
    'EAST_CHINA_LOGISTICS',
    '华东地区物流计费模板',
    'LOGISTICS_WAYBILL',
    '{"region":["EAST_CHINA"]}',
    20,
    1
);
```

**调用时传递维度信息**：

```json
{
  "bizNo": "ORDER_001",
  "bizType": "LOGISTICS_WAYBILL",
  "factors": {
    "weight": 30.5
  },
  "dimensions": {
    "region": "EAST_CHINA"
  },
  "dryRun": false
}
```

## 五、Java客户端调用示例

```java
@Service
@RequiredArgsConstructor
public class OrderService {

    private final RestTemplate restTemplate;

    public BillingResult calculateShippingFee(Order order) {
        // 构建计费请求
        BillingRequest request = new BillingRequest();
        request.setBizNo(order.getOrderNo());
        request.setBizType("LOGISTICS_WAYBILL");

        // 设置计费要素
        Map<String, Object> factors = new HashMap<>();
        factors.put("weight", order.getWeight());
        factors.put("distance", order.getDistance());
        request.setFactors(factors);

        // 设置维度条件
        Map<String, Object> dimensions = new HashMap<>();
        dimensions.put("region", order.getRegion());
        dimensions.put("timeType", "STANDARD");
        request.setDimensions(dimensions);

        // 试算模式
        request.setDryRun(false);

        // 调用计费接口
        String url = "http://billing-engine-service/api/billing/calculate";
        Result<BillingResult> result = restTemplate.postForObject(
            url, request, new ParameterizedTypeReference<Result<BillingResult>>() {});

        return result.getData();
    }
}
```

## 六、常见问题

### Q1：如何调试计费结果？

在计费结果中，每个计费项都包含了输入参数和计算结果，可以通过这些信息来验证计费是否正确。

### Q2：如何修改计费规则？

1. 修改公式配置（ladder_config或formula_expression）
2. 更新模板版本号
3. 清除Redis缓存：`redis-cli FLUSHDB`

### Q3：如何处理计费失败？

系统会记录失败原因到billing_record表的fail_reason字段，可以通过查询该字段来排查问题。

### Q4：如何实现多维度价格表查询？

使用TABLE_LOOKUP类型的公式，配置price_table_config字段：

```json
{
  "rules": [
    {
      "conditions": {
        "region": "EAST_CHINA",
        "timeType": "EXPRESS"
      },
      "price": 8.0
    },
    {
      "conditions": {
        "region": "EAST_CHINA",
        "timeType": "STANDARD"
      },
      "price": 5.0
    }
  ],
  "defaultPrice": 6.0
}
```

## 七、性能优化建议

1. **启用缓存**：公式、模板、要素都已配置Redis缓存
2. **批量计费**：如需要对多个订单计费，可实现批量接口
3. **异步计费**：对于非实时要求的场景，可使用MQ异步处理
4. **数据库优化**：确保billing_template、billing_item、billing_formula表建立了合适的索引
