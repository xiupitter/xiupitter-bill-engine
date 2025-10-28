package com.xiupitter.billing.excel.reader;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Excel数据监听器
 * 用于读取Excel数据并存储到List中
 *
 * @author xiupitter
 */
@Slf4j
@Getter
public class ExcelDataListener extends AnalysisEventListener<Map<Integer, Object>> {

    /**
     * 每隔5条存储数据库，实际使用中可以3000条，然后清理list，方便内存回收
     */
    private static final int BATCH_COUNT = 1000;

    /**
     * 存储读取的数据
     */
    private final List<Map<Integer, Object>> dataList = new ArrayList<>();

    /**
     * 表头信息
     */
    private Map<Integer, String> headMap;

    /**
     * 读取的行数
     */
    private int rowCount = 0;

    /**
     * 这里会一行行的返回头
     *
     * @param headMap 表头数据
     * @param context 上下文
     */
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        this.headMap = headMap;
        log.debug("解析到一条表头数据: {}", headMap);
    }

    /**
     * 这个每一条数据解析都会来调用
     *
     * @param data    一行数据
     * @param context 上下文
     */
    @Override
    public void invoke(Map<Integer, Object> data, AnalysisContext context) {
        log.debug("解析到一条数据: {}", data);
        dataList.add(data);
        rowCount++;

        // 达到BATCH_COUNT了，防止数据几万条数据在内存，可以分批处理
        if (dataList.size() >= BATCH_COUNT) {
            // 实际业务中可以在这里进行批量处理
            log.info("已读取 {} 条数据", rowCount);
        }
    }

    /**
     * 所有数据解析完成了会来调用
     *
     * @param context 上下文
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("所有数据解析完成，共 {} 条数据", rowCount);
    }

    /**
     * 获取表头Map
     */
    public Map<Integer, String> getHeadMap() {
        return headMap;
    }

    /**
     * 获取数据List
     */
    public List<Map<Integer, Object>> getDataList() {
        return dataList;
    }

    /**
     * 获取行数
     */
    public int getRowCount() {
        return rowCount;
    }
}
