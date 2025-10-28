package com.xiupitter.billing.template.controller;

import com.xiupitter.billing.api.dto.BillingTemplateDTO;
import com.xiupitter.billing.common.model.Result;
import com.xiupitter.billing.template.service.BillingTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 计费模板控制器
 *
 * @author xiupitter
 */
@RestController
@RequestMapping("/api/template")
@RequiredArgsConstructor
public class BillingTemplateController {

    private final BillingTemplateService billingTemplateService;

    /**
     * 根据模板编码查询模板详情
     */
    @GetMapping("/detail/{templateCode}")
    public Result<BillingTemplateDTO> getTemplateDetail(@PathVariable String templateCode) {
        BillingTemplateDTO template = billingTemplateService.getTemplateDetail(templateCode);
        return Result.success(template);
    }

    /**
     * 匹配计费模板
     */
    @PostMapping("/match/{bizType}")
    public Result<BillingTemplateDTO> matchTemplate(
            @PathVariable String bizType,
            @RequestBody Map<String, Object> dimensions) {

        BillingTemplateDTO template = billingTemplateService.matchTemplate(bizType, dimensions);
        if (template == null) {
            return Result.fail("未找到匹配的计费模板");
        }
        return Result.success(template);
    }
}
