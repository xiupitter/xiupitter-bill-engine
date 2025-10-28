package com.xiupitter.billing.template.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiupitter.billing.template.entity.BillingItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 计费项目Mapper
 *
 * @author xiupitter
 */
@Mapper
public interface BillingItemMapper extends BaseMapper<BillingItem> {
}
