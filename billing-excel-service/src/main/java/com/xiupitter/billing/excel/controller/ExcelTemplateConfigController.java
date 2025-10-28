package com.xiupitter.billing.excel.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiupitter.billing.common.model.Result;
import com.xiupitter.billing.excel.entity.ExcelTemplateConfig;
import com.xiupitter.billing.excel.service.ExcelTemplateConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * Excel模板配置管理接口
 *
 * @author xiupitter
 */
@Slf4j
@RestController
@RequestMapping("/api/excel/template")
@RequiredArgsConstructor
public class ExcelTemplateConfigController {

    private final ExcelTemplateConfigService templateConfigService;

    /**
     * 分页查询模板配置列表
     */
    @GetMapping("/list")
    public Result<Page<ExcelTemplateConfig>> listTemplates(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String templateName,
            @RequestParam(required = false) String bizType,
            @RequestParam(required = false) Integer status) {

        Page<ExcelTemplateConfig> page = new Page<>(current, size);
        Page<ExcelTemplateConfig> result = templateConfigService.listTemplates(
                page, templateName, bizType, status);

        return Result.success(result);
    }

    /**
     * 根据ID查询模板配置详情
     */
    @GetMapping("/{id}")
    public Result<ExcelTemplateConfig> getTemplate(@PathVariable Long id) {
        ExcelTemplateConfig config = templateConfigService.getById(id);
        if (config == null) {
            return Result.fail("模板配置不存在");
        }
        return Result.success(config);
    }

    /**
     * 创建模板配置
     */
    @PostMapping
    public Result<Long> createTemplate(@RequestBody ExcelTemplateConfig config) {
        try {
            Long templateId = templateConfigService.createTemplate(config);
            return Result.success("创建模板配置成功", templateId);
        } catch (Exception e) {
            log.error("创建模板配置失败", e);
            return Result.fail("创建模板配置失败: " + e.getMessage());
        }
    }

    /**
     * 更新模板配置
     */
    @PutMapping
    public Result<Void> updateTemplate(@RequestBody ExcelTemplateConfig config) {
        try {
            templateConfigService.updateTemplate(config);
            return Result.success("更新模板配置成功", null);
        } catch (Exception e) {
            log.error("更新模板配置失败", e);
            return Result.fail("更新模板配置失败: " + e.getMessage());
        }
    }

    /**
     * 删除模板配置
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteTemplate(@PathVariable Long id) {
        try {
            templateConfigService.deleteTemplate(id);
            return Result.success("删除模板配置成功", null);
        } catch (Exception e) {
            log.error("删除模板配置失败", e);
            return Result.fail("删除模板配置失败: " + e.getMessage());
        }
    }

    /**
     * 启用/禁用模板
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {

        try {
            templateConfigService.updateStatus(id, status);
            return Result.success("更新状态成功", null);
        } catch (Exception e) {
            log.error("更新状态失败", e);
            return Result.fail("更新状态失败: " + e.getMessage());
        }
    }

    /**
     * 根据文件名匹配模板
     */
    @GetMapping("/match")
    public Result<ExcelTemplateConfig> matchTemplate(
            @RequestParam String fileName,
            @RequestParam(required = false) String bizType) {

        ExcelTemplateConfig config = templateConfigService.matchTemplate(fileName, bizType);
        if (config == null) {
            return Result.fail("未找到匹配的模板配置");
        }
        return Result.success(config);
    }
}
