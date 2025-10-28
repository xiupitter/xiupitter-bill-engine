package com.xiupitter.billing.excel.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiupitter.billing.common.model.Result;
import com.xiupitter.billing.excel.entity.ExcelParseScript;
import com.xiupitter.billing.excel.service.ExcelParseScriptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Excel解析脚本管理接口
 *
 * @author xiupitter
 */
@Slf4j
@RestController
@RequestMapping("/api/excel/script")
@RequiredArgsConstructor
public class ExcelParseScriptController {

    private final ExcelParseScriptService parseScriptService;

    /**
     * 分页查询脚本列表
     */
    @GetMapping("/list")
    public Result<Page<ExcelParseScript>> listScripts(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String scriptName,
            @RequestParam(required = false) Integer status) {

        Page<ExcelParseScript> page = new Page<>(current, size);
        Page<ExcelParseScript> result = parseScriptService.listScripts(page, scriptName, status);

        return Result.success(result);
    }

    /**
     * 根据ID查询脚本详情
     */
    @GetMapping("/{id}")
    public Result<ExcelParseScript> getScript(@PathVariable Long id) {
        ExcelParseScript script = parseScriptService.getById(id);
        if (script == null) {
            return Result.fail("脚本不存在");
        }
        return Result.success(script);
    }

    /**
     * 创建脚本
     */
    @PostMapping
    public Result<Long> createScript(@RequestBody ExcelParseScript script) {
        try {
            Long scriptId = parseScriptService.createScript(script);
            return Result.success("创建脚本成功", scriptId);
        } catch (Exception e) {
            log.error("创建脚本失败", e);
            return Result.fail("创建脚本失败: " + e.getMessage());
        }
    }

    /**
     * 更新脚本
     */
    @PutMapping
    public Result<Void> updateScript(@RequestBody ExcelParseScript script) {
        try {
            parseScriptService.updateScript(script);
            return Result.success("更新脚本成功", null);
        } catch (Exception e) {
            log.error("更新脚本失败", e);
            return Result.fail("更新脚本失败: " + e.getMessage());
        }
    }

    /**
     * 删除脚本
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteScript(@PathVariable Long id) {
        try {
            parseScriptService.deleteScript(id);
            return Result.success("删除脚本成功", null);
        } catch (Exception e) {
            log.error("删除脚本失败", e);
            return Result.fail("删除脚本失败: " + e.getMessage());
        }
    }

    /**
     * 测试脚本
     */
    @PostMapping("/test")
    public Result<Map<String, Object>> testScript(
            @RequestParam Long scriptId,
            @RequestBody Map<String, Object> testData) {

        try {
            Map<String, Object> result = parseScriptService.testScript(scriptId, testData);
            return Result.success(result);
        } catch (Exception e) {
            log.error("测试脚本失败", e);
            return Result.fail("测试脚本失败: " + e.getMessage());
        }
    }

    /**
     * 启用/禁用脚本
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {

        try {
            parseScriptService.updateStatus(id, status);
            return Result.success("更新状态成功", null);
        } catch (Exception e) {
            log.error("更新状态失败", e);
            return Result.fail("更新状态失败: " + e.getMessage());
        }
    }
}
