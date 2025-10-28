package com.xiupitter.billing.excel.service;

import com.alibaba.fastjson2.JSON;
import com.xiupitter.billing.excel.engine.ScriptEngine;
import com.xiupitter.billing.excel.entity.ExcelImportRecord;
import com.xiupitter.billing.excel.entity.ExcelParseScript;
import com.xiupitter.billing.excel.entity.ExcelTemplateConfig;
import com.xiupitter.billing.excel.mapper.ExcelImportRecordMapper;
import com.xiupitter.billing.excel.reader.ExcelDataListener;
import com.xiupitter.billing.excel.reader.ExcelReaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Excel解析服务
 * 核心服务，负责Excel文件的解析和数据提取
 *
 * @author xiupitter
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelParseService {

    private final ExcelReaderService excelReader;
    private final ScriptEngine scriptEngine;
    private final ExcelTemplateConfigService templateConfigService;
    private final ExcelParseScriptService parseScriptService;
    private final ExcelImportRecordMapper importRecordMapper;

    /**
     * 解析Excel文件
     *
     * @param file     上传的Excel文件
     * @param bizType  业务类型（可选）
     * @return 解析结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> parseExcel(MultipartFile file, String bizType) {
        long startTime = System.currentTimeMillis();

        try {
            // 1. 根据文件名匹配模板配置
            String fileName = file.getOriginalFilename();
            ExcelTemplateConfig templateConfig = templateConfigService.matchTemplate(fileName, bizType);

            if (templateConfig == null) {
                throw new RuntimeException("未找到匹配的Excel模板配置，文件名: " + fileName);
            }

            // 2. 获取解析脚本
            ExcelParseScript parseScript = parseScriptService.getById(templateConfig.getParseScriptId());
            if (parseScript == null) {
                throw new RuntimeException("解析脚本不存在，脚本ID: " + templateConfig.getParseScriptId());
            }

            if (parseScript.getStatus() != 1) {
                throw new RuntimeException("解析脚本未启用，脚本编码: " + parseScript.getScriptCode());
            }

            // 3. 读取Excel文件
            InputStream inputStream = file.getInputStream();
            ExcelDataListener listener = excelReader.readExcel(
                    inputStream,
                    templateConfig.getSheetName(),
                    templateConfig.getHeaderRow()
            );

            List<Map<Integer, Object>> dataList = listener.getDataList();
            log.info("Excel文件读取成功，文件名: {}, 总行数: {}", fileName, dataList.size());

            // 4. 使用脚本解析每一行数据
            List<Map<String, Object>> parsedDataList = new ArrayList<>();
            List<Map<String, Object>> errorList = new ArrayList<>();

            for (int i = 0; i < dataList.size(); i++) {
                Map<Integer, Object> rowData = dataList.get(i);
                try {
                    Map<String, Object> parsedData = scriptEngine.executeParseScript(
                            parseScript.getScriptContent(),
                            rowData
                    );
                    parsedData.put("_rowIndex", i + templateConfig.getDataStartRow());
                    parsedDataList.add(parsedData);
                } catch (Exception e) {
                    log.error("解析行数据失败，行号: {}", i + templateConfig.getDataStartRow(), e);
                    Map<String, Object> error = new HashMap<>();
                    error.put("rowIndex", i + templateConfig.getDataStartRow());
                    error.put("rowData", rowData);
                    error.put("error", e.getMessage());
                    errorList.add(error);
                }
            }

            long endTime = System.currentTimeMillis();
            int costTime = (int) (endTime - startTime);

            // 5. 保存导入记录
            ExcelImportRecord importRecord = new ExcelImportRecord();
            importRecord.setImportNo(generateImportNo());
            importRecord.setExcelTemplateId(templateConfig.getId());
            importRecord.setFileName(fileName);
            importRecord.setFileSize(file.getSize());
            importRecord.setTotalRows(dataList.size());
            importRecord.setSuccessRows(parsedDataList.size());
            importRecord.setFailRows(errorList.size());
            importRecord.setParseResult(JSON.toJSONString(parsedDataList));
            importRecord.setErrorDetails(errorList.isEmpty() ? null : JSON.toJSONString(errorList));
            importRecord.setImportTime(LocalDateTime.now());
            importRecord.setImportStatus(errorList.isEmpty() ? 1 : 2); // 1-成功, 2-部分成功
            importRecord.setCostTime(costTime);

            importRecordMapper.insert(importRecord);

            // 6. 返回解析结果
            Map<String, Object> result = new HashMap<>();
            result.put("importNo", importRecord.getImportNo());
            result.put("templateCode", templateConfig.getTemplateCode());
            result.put("templateName", templateConfig.getTemplateName());
            result.put("scriptCode", parseScript.getScriptCode());
            result.put("scriptName", parseScript.getScriptName());
            result.put("totalRows", dataList.size());
            result.put("successRows", parsedDataList.size());
            result.put("failRows", errorList.size());
            result.put("parsedData", parsedDataList);
            result.put("errors", errorList);
            result.put("costTime", costTime);

            log.info("Excel解析完成，导入批次号: {}, 总行数: {}, 成功: {}, 失败: {}, 耗时: {}ms",
                    importRecord.getImportNo(), dataList.size(), parsedDataList.size(),
                    errorList.size(), costTime);

            return result;

        } catch (Exception e) {
            log.error("Excel解析失败", e);

            // 保存失败记录
            ExcelImportRecord failRecord = new ExcelImportRecord();
            failRecord.setImportNo(generateImportNo());
            failRecord.setFileName(file.getOriginalFilename());
            failRecord.setFileSize(file.getSize());
            failRecord.setImportTime(LocalDateTime.now());
            failRecord.setImportStatus(3); // 3-失败
            failRecord.setErrorDetails(JSON.toJSONString(Collections.singletonMap("error", e.getMessage())));
            failRecord.setCostTime((int) (System.currentTimeMillis() - startTime));

            importRecordMapper.insert(failRecord);

            throw new RuntimeException("Excel解析失败: " + e.getMessage(), e);
        }
    }

    /**
     * 查询导入记录
     *
     * @param importNo 导入批次号
     * @return 导入记录
     */
    public ExcelImportRecord getImportRecord(String importNo) {
        return importRecordMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ExcelImportRecord>()
                        .eq(ExcelImportRecord::getImportNo, importNo)
                        .eq(ExcelImportRecord::getDeleted, 0)
        );
    }

    /**
     * 生成导入批次号
     *
     * @return 导入批次号
     */
    private String generateImportNo() {
        return "IMP" + System.currentTimeMillis() + String.format("%04d", new Random().nextInt(10000));
    }
}
