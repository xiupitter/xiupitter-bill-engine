package com.xiupitter.billing.template.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiupitter.billing.api.dto.BillingTemplateDTO;
import com.xiupitter.billing.template.engine.TemplateMatchEngine;
import com.xiupitter.billing.template.entity.BillingItem;
import com.xiupitter.billing.template.entity.BillingTemplate;
import com.xiupitter.billing.template.mapper.BillingItemMapper;
import com.xiupitter.billing.template.mapper.BillingTemplateMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 计费模板服务
 *
 * @author xiupitter
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BillingTemplateService extends ServiceImpl<BillingTemplateMapper, BillingTemplate> {

    private final BillingItemMapper billingItemMapper;
    private final TemplateMatchEngine templateMatchEngine;

    /**
     * 根据模板编码查询模板详情（带计费项）
     */
    @Cacheable(value = "billing:template:detail", key = "#templateCode")
    public BillingTemplateDTO getTemplateDetail(String templateCode) {
        // 1. 查询模板基本信息
        BillingTemplate template = this.getOne(new LambdaQueryWrapper<BillingTemplate>()
                .eq(BillingTemplate::getTemplateCode, templateCode)
                .eq(BillingTemplate::getStatus, 1));

        if (template == null) {
            return null;
        }

        // 2. 查询模板下的所有计费项
        List<BillingItem> items = billingItemMapper.selectList(new LambdaQueryWrapper<BillingItem>()
                .eq(BillingItem::getTemplateId, template.getId())
                .eq(BillingItem::getStatus, 1)
                .orderByAsc(BillingItem::getSortOrder));

        // 3. 组装DTO
        return buildTemplateDTO(template, items);
    }

    /**
     * 根据模板ID查询模板详情（带计费项）
     */
    @Cacheable(value = "billing:template:detail:id", key = "#templateId")
    public BillingTemplateDTO getTemplateDetailById(Long templateId) {
        BillingTemplate template = this.getById(templateId);
        if (template == null) {
            return null;
        }

        List<BillingItem> items = billingItemMapper.selectList(new LambdaQueryWrapper<BillingItem>()
                .eq(BillingItem::getTemplateId, templateId)
                .eq(BillingItem::getStatus, 1)
                .orderByAsc(BillingItem::getSortOrder));

        return buildTemplateDTO(template, items);
    }

    /**
     * 匹配计费模板
     *
     * @param bizType    业务类型
     * @param dimensions 维度条件
     * @return 匹配的模板详情
     */
    public BillingTemplateDTO matchTemplate(String bizType, Map<String, Object> dimensions) {
        log.info("匹配计费模板, bizType={}, dimensions={}", bizType, dimensions);

        BillingTemplate template = templateMatchEngine.matchTemplate(bizType, dimensions);

        if (template == null) {
            log.warn("未匹配到计费模板, bizType={}", bizType);
            return null;
        }

        return getTemplateDetailById(template.getId());
    }

    /**
     * 保存或更新模板
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"billing:template:detail", "billing:template:detail:id"}, allEntries = true)
    public boolean saveOrUpdateTemplate(BillingTemplate template, List<BillingItem> items) {
        // 1. 保存模板
        this.saveOrUpdate(template);

        // 2. 删除旧的计费项
        if (template.getId() != null) {
            billingItemMapper.delete(new LambdaQueryWrapper<BillingItem>()
                    .eq(BillingItem::getTemplateId, template.getId()));
        }

        // 3. 保存新的计费项
        if (items != null && !items.isEmpty()) {
            for (BillingItem item : items) {
                item.setTemplateId(template.getId());
                billingItemMapper.insert(item);
            }
        }

        return true;
    }

    /**
     * 删除模板
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"billing:template:detail", "billing:template:detail:id"}, allEntries = true)
    public boolean deleteTemplate(Long templateId) {
        // 1. 删除计费项
        billingItemMapper.delete(new LambdaQueryWrapper<BillingItem>()
                .eq(BillingItem::getTemplateId, templateId));

        // 2. 删除模板
        return this.removeById(templateId);
    }

    /**
     * 组装模板DTO
     */
    private BillingTemplateDTO buildTemplateDTO(BillingTemplate template, List<BillingItem> items) {
        BillingTemplateDTO dto = new BillingTemplateDTO();
        BeanUtils.copyProperties(template, dto);

        // 转换计费项
        List<BillingTemplateDTO.BillingItemDTO> itemDTOs = items.stream()
                .map(item -> {
                    BillingTemplateDTO.BillingItemDTO itemDTO = new BillingTemplateDTO.BillingItemDTO();
                    BeanUtils.copyProperties(item, itemDTO);
                    return itemDTO;
                })
                .collect(Collectors.toList());

        dto.setItems(itemDTOs);
        return dto;
    }
}
