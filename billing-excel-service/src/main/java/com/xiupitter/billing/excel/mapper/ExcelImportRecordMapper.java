package com.xiupitter.billing.excel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiupitter.billing.excel.entity.ExcelImportRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * Excel导入记录Mapper
 *
 * @author xiupitter
 */
@Mapper
public interface ExcelImportRecordMapper extends BaseMapper<ExcelImportRecord> {
}
