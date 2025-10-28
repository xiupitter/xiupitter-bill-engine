package com.xiupitter.billing.excel.controller;

import com.xiupitter.billing.common.model.Result;
import com.xiupitter.billing.excel.entity.ExcelImportRecord;
import com.xiupitter.billing.excel.service.ExcelParseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Excel解析接口
 *
 * @author xiupitter
 */
@Slf4j
@RestController
@RequestMapping("/api/excel/parse")
@RequiredArgsConstructor
public class ExcelParseController {

    private final ExcelParseService excelParseService;

    /**
     * 上传并解析Excel文件
     *
     * @param file    Excel文件
     * @param bizType 业务类型（可选）
     * @return 解析结果
     */
    @PostMapping("/upload")
    public Result<Map<String, Object>> uploadAndParse(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String bizType) {

        if (file.isEmpty()) {
            return Result.fail("文件不能为空");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xls") && !fileName.endsWith(".xlsx"))) {
            return Result.fail("文件格式不正确，只支持.xls和.xlsx格式");
        }

        try {
            Map<String, Object> result = excelParseService.parseExcel(file, bizType);
            return Result.success("Excel解析成功", result);

        } catch (Exception e) {
            log.error("Excel解析失败", e);
            return Result.fail("Excel解析失败: " + e.getMessage());
        }
    }

    /**
     * 查询导入记录
     *
     * @param importNo 导入批次号
     * @return 导入记录
     */
    @GetMapping("/record/{importNo}")
    public Result<ExcelImportRecord> getImportRecord(@PathVariable String importNo) {
        try {
            ExcelImportRecord record = excelParseService.getImportRecord(importNo);
            if (record == null) {
                return Result.fail("导入记录不存在");
            }
            return Result.success(record);

        } catch (Exception e) {
            log.error("查询导入记录失败", e);
            return Result.fail("查询导入记录失败: " + e.getMessage());
        }
    }
}
