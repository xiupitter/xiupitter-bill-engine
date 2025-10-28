package com.xiupitter.billing.excel.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiupitter.billing.excel.entity.ExcelTemplateConfig;
import com.xiupitter.billing.excel.mapper.ExcelTemplateConfigMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Excel模板配置服务
 *
 * @author xiupitter
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelTemplateConfigService {

    private final ExcelTemplateConfigMapper templateConfigMapper;

    /**
     * 根据ID查询模板配置
     *
     * @param templateId 模板ID
     * @return 模板配置
     */
    public ExcelTemplateConfig getById(Long templateId) {
        return templateConfigMapper.selectById(templateId);
    }

    /**
     * 根据模板编码查询模板配置
     *
     * @param templateCode 模板编码
     * @return 模板配置
     */
    public ExcelTemplateConfig getByCode(String templateCode) {
        LambdaQueryWrapper<ExcelTemplateConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExcelTemplateConfig::getTemplateCode, templateCode);
        wrapper.eq(ExcelTemplateConfig::getDeleted, 0);
        return templateConfigMapper.selectOne(wrapper);
    }

    /**
     * 根据文件名匹配模板配置
     *
     * @param fileName 文件名
     * @param bizType  业务类型（可选）
     * @return 匹配的模板配置
     */
    public ExcelTemplateConfig matchTemplate(String fileName, String bizType) {
        LambdaQueryWrapper<ExcelTemplateConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExcelTemplateConfig::getStatus, 1);
        wrapper.eq(ExcelTemplateConfig::getDeleted, 0);

        if (bizType != null && !bizType.isEmpty()) {
            wrapper.eq(ExcelTemplateConfig::getBizType, bizType);
        }

        wrapper.orderByDesc(ExcelTemplateConfig::getPriority);

        List<ExcelTemplateConfig> templates = templateConfigMapper.selectList(wrapper);

        // 遍历模板，匹配文件名模式
        for (ExcelTemplateConfig template : templates) {
            String pattern = template.getFileNamePattern();
            if (pattern != null && !pattern.isEmpty()) {
                try {
                    if (Pattern.matches(pattern, fileName)) {
                        log.info("匹配到模板，模板编码: {}, 文件名: {}", template.getTemplateCode(), fileName);
                        return template;
                    }
                } catch (Exception e) {
                    log.warn("文件名模式匹配失败，模板: {}, 模式: {}", template.getTemplateCode(), pattern, e);
                }
            }
        }

        log.warn("未找到匹配的模板，文件名: {}, 业务类型: {}", fileName, bizType);
        return null;
    }

    /**
     * 分页查询模板配置列表
     *
     * @param page         分页对象
     * @param templateName 模板名称（模糊查询）
     * @param bizType      业务类型
     * @param status       状态
     * @return 分页结果
     */
    public Page<ExcelTemplateConfig> listTemplates(Page<ExcelTemplateConfig> page, String templateName,
                                                    String bizType, Integer status) {
        LambdaQueryWrapper<ExcelTemplateConfig> wrapper = new LambdaQueryWrapper<>();

        if (templateName != null && !templateName.isEmpty()) {
            wrapper.like(ExcelTemplateConfig::getTemplateName, templateName);
        }

        if (bizType != null && !bizType.isEmpty()) {
            wrapper.eq(ExcelTemplateConfig::getBizType, bizType);
        }

        if (status != null) {
            wrapper.eq(ExcelTemplateConfig::getStatus, status);
        }

        wrapper.eq(ExcelTemplateConfig::getDeleted, 0);
        wrapper.orderByDesc(ExcelTemplateConfig::getPriority);
        wrapper.orderByDesc(ExcelTemplateConfig::getCreateTime);

        return templateConfigMapper.selectPage(page, wrapper);
    }

    /**
     * 创建模板配置
     *
     * @param config 模板配置
     * @return 模板ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createTemplate(ExcelTemplateConfig config) {
        // 检查模板编码是否已存在
        ExcelTemplateConfig existing = getByCode(config.getTemplateCode());
        if (existing != null) {
            throw new RuntimeException("模板编码已存在: " + config.getTemplateCode());
        }

        // 设置默认值
        if (config.getHeaderRow() == null) {
            config.setHeaderRow(1);
        }
        if (config.getDataStartRow() == null) {
            config.setDataStartRow(2);
        }
        if (config.getPriority() == null) {
            config.setPriority(0);
        }
        if (config.getStatus() == null) {
            config.setStatus(1);
        }

        templateConfigMapper.insert(config);
        log.info("创建模板配置成功，模板ID: {}, 模板编码: {}", config.getId(), config.getTemplateCode());

        return config.getId();
    }

    /**
     * 更新模板配置
     *
     * @param config 模板配置
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateTemplate(ExcelTemplateConfig config) {
        ExcelTemplateConfig existing = templateConfigMapper.selectById(config.getId());
        if (existing == null) {
            throw new RuntimeException("模板配置不存在，ID: " + config.getId());
        }

        templateConfigMapper.updateById(config);
        log.info("更新模板配置成功，模板ID: {}", config.getId());
    }

    /**
     * 删除模板配置（逻辑删除）
     *
     * @param templateId 模板ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteTemplate(Long templateId) {
        ExcelTemplateConfig config = templateConfigMapper.selectById(templateId);
        if (config == null) {
            throw new RuntimeException("模板配置不存在，ID: " + templateId);
        }

        config.setDeleted(1);
        templateConfigMapper.updateById(config);

        log.info("删除模板配置成功，模板ID: {}", templateId);
    }

    /**
     * 启用/禁用模板
     *
     * @param templateId 模板ID
     * @param status     状态：0-禁用, 1-启用
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long templateId, Integer status) {
        ExcelTemplateConfig config = templateConfigMapper.selectById(templateId);
        if (config == null) {
            throw new RuntimeException("模板配置不存在，ID: " + templateId);
        }

        config.setStatus(status);
        templateConfigMapper.updateById(config);

        log.info("更新模板状态成功，模板ID: {}, 状态: {}", templateId, status);
    }
}
