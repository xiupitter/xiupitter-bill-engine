package com.xiupitter.billing.api.feign;

import com.xiupitter.billing.common.model.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 公式服务Feign客户端
 *
 * @author xiupitter
 */
@FeignClient(name = "billing-formula-service", path = "/api/formula")
public interface FormulaServiceClient {

    /**
     * 执行公式计算
     *
     * @param formulaId 公式ID
     * @param context   计算上下文
     * @return 计算结果
     */
    @PostMapping("/calculate")
    Result<BigDecimal> calculate(
            @RequestParam("formulaId") Long formulaId,
            @RequestBody Map<String, Object> context);
}
