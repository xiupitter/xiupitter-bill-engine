package com.xiupitter.billing.engine.controller;

import com.xiupitter.billing.api.dto.BillingRequest;
import com.xiupitter.billing.api.dto.BillingResult;
import com.xiupitter.billing.common.model.Result;
import com.xiupitter.billing.engine.entity.BillingRecord;
import com.xiupitter.billing.engine.service.BillingEngineService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 计费引擎控制器
 *
 * @author xiupitter
 */
@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
public class BillingEngineController {

    private final BillingEngineService billingEngineService;

    /**
     * 执行计费
     */
    @PostMapping("/calculate")
    public Result<BillingResult> calculate(@Validated @RequestBody BillingRequest request) {
        BillingResult result = billingEngineService.billing(request);
        return Result.success(result);
    }

    /**
     * 根据业务单号查询计费记录
     */
    @GetMapping("/record/{bizNo}")
    public Result<BillingRecord> getRecord(@PathVariable String bizNo) {
        BillingRecord record = billingEngineService.getByBizNo(bizNo);
        return Result.success(record);
    }
}
