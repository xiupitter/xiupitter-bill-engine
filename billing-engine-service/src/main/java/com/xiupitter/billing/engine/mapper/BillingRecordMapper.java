package com.xiupitter.billing.engine.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiupitter.billing.engine.entity.BillingRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 计费记录Mapper
 *
 * @author xiupitter
 */
@Mapper
public interface BillingRecordMapper extends BaseMapper<BillingRecord> {

    /**
     * 根据业务单号查询
     */
    @Select("SELECT * FROM billing_record WHERE biz_no = #{bizNo} AND deleted = 0 ORDER BY create_time DESC LIMIT 1")
    BillingRecord selectByBizNo(String bizNo);
}
