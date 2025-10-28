package com.xiupitter.billing.factor.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiupitter.billing.factor.entity.BillingFactor;
import com.xiupitter.billing.factor.mapper.BillingFactorMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 计费要素服务
 *
 * @author xiupitter
 */
@Slf4j
@Service
public class BillingFactorService extends ServiceImpl<BillingFactorMapper, BillingFactor> {

    /**
     * 分页查询计费要素
     */
    public Page<BillingFactor> pageQuery(Page<BillingFactor> page, String bizType, String factorType, String keyword) {
        LambdaQueryWrapper<BillingFactor> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(bizType)) {
            wrapper.eq(BillingFactor::getBizType, bizType);
        }

        if (StringUtils.hasText(factorType)) {
            wrapper.eq(BillingFactor::getFactorType, factorType);
        }

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(BillingFactor::getFactorCode, keyword)
                    .or()
                    .like(BillingFactor::getFactorName, keyword));
        }

        wrapper.orderByAsc(BillingFactor::getSortOrder)
                .orderByDesc(BillingFactor::getCreateTime);

        return this.page(page, wrapper);
    }

    /**
     * 根据要素编码查询（带缓存）
     */
    @Cacheable(value = "billing:factor", key = "#factorCode")
    public BillingFactor getByFactorCode(String factorCode) {
        return this.getOne(new LambdaQueryWrapper<BillingFactor>()
                .eq(BillingFactor::getFactorCode, factorCode)
                .eq(BillingFactor::getStatus, 1));
    }

    /**
     * 根据业务类型查询所有要素
     */
    @Cacheable(value = "billing:factor:list", key = "#bizType")
    public List<BillingFactor> listByBizType(String bizType) {
        return this.list(new LambdaQueryWrapper<BillingFactor>()
                .eq(BillingFactor::getBizType, bizType)
                .eq(BillingFactor::getStatus, 1)
                .orderByAsc(BillingFactor::getSortOrder));
    }

    /**
     * 创建或更新要素
     */
    @CacheEvict(value = {"billing:factor", "billing:factor:list"}, allEntries = true)
    public boolean saveOrUpdateFactor(BillingFactor factor) {
        // 检查编码唯一性
        if (factor.getId() == null) {
            long count = this.count(new LambdaQueryWrapper<BillingFactor>()
                    .eq(BillingFactor::getFactorCode, factor.getFactorCode()));
            if (count > 0) {
                throw new RuntimeException("要素编码已存在：" + factor.getFactorCode());
            }
        }

        return this.saveOrUpdate(factor);
    }

    /**
     * 删除要素
     */
    @CacheEvict(value = {"billing:factor", "billing:factor:list"}, allEntries = true)
    public boolean deleteFactor(Long id) {
        return this.removeById(id);
    }

    /**
     * 批量导入要素
     */
    @CacheEvict(value = {"billing:factor", "billing:factor:list"}, allEntries = true)
    public boolean batchImport(List<BillingFactor> factors) {
        return this.saveBatch(factors);
    }
}
