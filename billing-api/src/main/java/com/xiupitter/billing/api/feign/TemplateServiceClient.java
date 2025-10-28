package com.xiupitter.billing.api.feign;

import com.xiupitter.billing.api.dto.BillingTemplateDTO;
import com.xiupitter.billing.common.model.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * 模板服务Feign客户端
 *
 * @author xiupitter
 */
@FeignClient(name = "billing-template-service", path = "/api/template")
public interface TemplateServiceClient {

    /**
     * 根据模板编码查询模板详情
     */
    @GetMapping("/detail/{templateCode}")
    Result<BillingTemplateDTO> getTemplateDetail(@PathVariable("templateCode") String templateCode);

    /**
     * 匹配计费模板
     */
    @PostMapping("/match/{bizType}")
    Result<BillingTemplateDTO> matchTemplate(
            @PathVariable("bizType") String bizType,
            @RequestBody Map<String, Object> dimensions);
}
