package com.xiupitter.billing.factor.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiupitter.billing.common.model.Result;
import com.xiupitter.billing.factor.entity.BillingFactor;
import com.xiupitter.billing.factor.service.BillingFactorService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 计费要素控制器
 *
 * @author xiupitter
 */
@RestController
@RequestMapping("/api/factor")
@RequiredArgsConstructor
public class BillingFactorController {

    private final BillingFactorService billingFactorService;

    /**
     * 分页查询计费要素
     */
    @GetMapping("/page")
    public Result<Page<BillingFactor>> page(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String bizType,
            @RequestParam(required = false) String factorType,
            @RequestParam(required = false) String keyword) {

        Page<BillingFactor> page = new Page<>(current, size);
        return Result.success(billingFactorService.pageQuery(page, bizType, factorType, keyword));
    }

    /**
     * 根据ID查询要素
     */
    @GetMapping("/{id}")
    public Result<BillingFactor> getById(@PathVariable Long id) {
        return Result.success(billingFactorService.getById(id));
    }

    /**
     * 根据要素编码查询
     */
    @GetMapping("/code/{factorCode}")
    public Result<BillingFactor> getByCode(@PathVariable String factorCode) {
        return Result.success(billingFactorService.getByFactorCode(factorCode));
    }

    /**
     * 根据业务类型查询所有要素
     */
    @GetMapping("/list/{bizType}")
    public Result<List<BillingFactor>> listByBizType(@PathVariable String bizType) {
        return Result.success(billingFactorService.listByBizType(bizType));
    }

    /**
     * 创建或更新要素
     */
    @PostMapping
    public Result<Boolean> save(@Validated @RequestBody BillingFactor factor) {
        return Result.success(billingFactorService.saveOrUpdateFactor(factor));
    }

    /**
     * 删除要素
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(billingFactorService.deleteFactor(id));
    }

    /**
     * 批量导入要素
     */
    @PostMapping("/batch")
    public Result<Boolean> batchImport(@RequestBody List<BillingFactor> factors) {
        return Result.success(billingFactorService.batchImport(factors));
    }
}
