package com.xiupitter.billing.excel.reader;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Excel读取服务
 * 基于EasyExcel实现Excel文件读取
 *
 * @author xiupitter
 */
@Slf4j
@Component
public class ExcelReaderService {

    /**
     * 读取Excel文件（读取第一个Sheet）
     *
     * @param inputStream 文件输入流
     * @return Excel数据监听器（包含读取的数据）
     */
    public ExcelDataListener readExcel(InputStream inputStream) {
        return readExcel(inputStream, null, 1);
    }

    /**
     * 读取Excel文件（指定Sheet名称）
     *
     * @param inputStream 文件输入流
     * @param sheetName   Sheet名称
     * @param headerRow   表头所在行号（从1开始）
     * @return Excel数据监听器（包含读取的数据）
     */
    public ExcelDataListener readExcel(InputStream inputStream, String sheetName, Integer headerRow) {
        try {
            ExcelDataListener listener = new ExcelDataListener();

            // 创建ExcelReader
            ExcelReader excelReader = EasyExcel.read(inputStream, listener)
                    .headRowNumber(headerRow != null ? headerRow : 1)
                    .build();

            // 构建ReadSheet
            ReadSheet readSheet;
            if (sheetName != null && !sheetName.isEmpty()) {
                readSheet = EasyExcel.readSheet(sheetName).build();
            } else {
                readSheet = EasyExcel.readSheet(0).build();
            }

            // 读取数据
            excelReader.read(readSheet);

            // 关闭资源
            excelReader.finish();

            log.info("Excel文件读取成功，共读取 {} 条数据", listener.getRowCount());

            return listener;

        } catch (Exception e) {
            log.error("Excel文件读取失败", e);
            throw new RuntimeException("Excel文件读取失败: " + e.getMessage(), e);
        }
    }

    /**
     * 读取Excel文件（指定Sheet索引，从0开始）
     *
     * @param inputStream 文件输入流
     * @param sheetIndex  Sheet索引（从0开始）
     * @param headerRow   表头所在行号（从1开始）
     * @return Excel数据监听器（包含读取的数据）
     */
    public ExcelDataListener readExcel(InputStream inputStream, int sheetIndex, Integer headerRow) {
        try {
            ExcelDataListener listener = new ExcelDataListener();

            // 创建ExcelReader
            ExcelReader excelReader = EasyExcel.read(inputStream, listener)
                    .headRowNumber(headerRow != null ? headerRow : 1)
                    .build();

            // 构建ReadSheet
            ReadSheet readSheet = EasyExcel.readSheet(sheetIndex).build();

            // 读取数据
            excelReader.read(readSheet);

            // 关闭资源
            excelReader.finish();

            log.info("Excel文件读取成功，共读取 {} 条数据", listener.getRowCount());

            return listener;

        } catch (Exception e) {
            log.error("Excel文件读取失败", e);
            throw new RuntimeException("Excel文件读取失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取Excel文件的所有Sheet名称
     *
     * @param inputStream 文件输入流
     * @return Sheet名称列表
     */
    public List<String> getSheetNames(InputStream inputStream) {
        try {
            ExcelReader excelReader = EasyExcel.read(inputStream).build();
            List<ReadSheet> sheets = excelReader.excelExecutor().sheetList();
            excelReader.finish();

            return sheets.stream()
                    .map(ReadSheet::getSheetName)
                    .collect(java.util.stream.Collectors.toList());

        } catch (Exception e) {
            log.error("获取Sheet名称失败", e);
            throw new RuntimeException("获取Sheet名称失败: " + e.getMessage(), e);
        }
    }
}
