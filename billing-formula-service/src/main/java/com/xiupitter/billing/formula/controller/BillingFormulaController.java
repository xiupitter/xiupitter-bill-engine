package com.xiupitter.billing.formula.controller;

import com.xiupitter.billing.common.model.Result;
import com.xiupitter.billing.formula.service.BillingFormulaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 计费公式控制器
 *
 * @author xiupitter
 */
@RestController
@RequestMapping("/api/formula")
@RequiredArgsConstructor
public class BillingFormulaController {

    private final BillingFormulaService billingFormulaService;

    /**
     * 执行公式计算
     */
    @PostMapping("/calculate")
    public Result<BigDecimal> calculate(
            @RequestParam("formulaId") Long formulaId,
            @RequestBody Map<String, Object> context) {

        BigDecimal result = billingFormulaService.calculate(formulaId, context);
        return Result.success(result);
    }
}
