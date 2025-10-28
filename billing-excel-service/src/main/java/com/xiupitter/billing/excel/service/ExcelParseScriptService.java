package com.xiupitter.billing.excel.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiupitter.billing.excel.engine.ScriptEngine;
import com.xiupitter.billing.excel.entity.ExcelParseScript;
import com.xiupitter.billing.excel.mapper.ExcelParseScriptMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Excel解析脚本服务
 *
 * @author xiupitter
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelParseScriptService {

    private final ExcelParseScriptMapper scriptMapper;
    private final ScriptEngine scriptEngine;

    /**
     * 根据脚本ID查询脚本
     *
     * @param scriptId 脚本ID
     * @return 脚本实体
     */
    public ExcelParseScript getById(Long scriptId) {
        return scriptMapper.selectById(scriptId);
    }

    /**
     * 根据脚本编码查询脚本
     *
     * @param scriptCode 脚本编码
     * @return 脚本实体
     */
    public ExcelParseScript getByCode(String scriptCode) {
        LambdaQueryWrapper<ExcelParseScript> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExcelParseScript::getScriptCode, scriptCode);
        wrapper.eq(ExcelParseScript::getDeleted, 0);
        return scriptMapper.selectOne(wrapper);
    }

    /**
     * 分页查询脚本列表
     *
     * @param page       分页对象
     * @param scriptName 脚本名称（模糊查询）
     * @param status     状态
     * @return 分页结果
     */
    public Page<ExcelParseScript> listScripts(Page<ExcelParseScript> page, String scriptName, Integer status) {
        LambdaQueryWrapper<ExcelParseScript> wrapper = new LambdaQueryWrapper<>();

        if (scriptName != null && !scriptName.isEmpty()) {
            wrapper.like(ExcelParseScript::getScriptName, scriptName);
        }

        if (status != null) {
            wrapper.eq(ExcelParseScript::getStatus, status);
        }

        wrapper.eq(ExcelParseScript::getDeleted, 0);
        wrapper.orderByDesc(ExcelParseScript::getCreateTime);

        return scriptMapper.selectPage(page, wrapper);
    }

    /**
     * 创建脚本
     *
     * @param script 脚本实体
     * @return 脚本ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createScript(ExcelParseScript script) {
        // 检查脚本编码是否已存在
        ExcelParseScript existing = getByCode(script.getScriptCode());
        if (existing != null) {
            throw new RuntimeException("脚本编码已存在: " + script.getScriptCode());
        }

        // 设置默认值
        if (script.getScriptType() == null) {
            script.setScriptType("MVEL");
        }
        if (script.getVersion() == null) {
            script.setVersion(1);
        }
        if (script.getStatus() == null) {
            script.setStatus(1);
        }

        scriptMapper.insert(script);
        log.info("创建脚本成功，脚本ID: {}, 脚本编码: {}", script.getId(), script.getScriptCode());

        return script.getId();
    }

    /**
     * 更新脚本
     *
     * @param script 脚本实体
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateScript(ExcelParseScript script) {
        ExcelParseScript existing = scriptMapper.selectById(script.getId());
        if (existing == null) {
            throw new RuntimeException("脚本不存在，ID: " + script.getId());
        }

        // 如果脚本内容变化，清除缓存并增加版本号
        if (!existing.getScriptContent().equals(script.getScriptContent())) {
            scriptEngine.removeCache(existing.getScriptContent());
            script.setVersion(existing.getVersion() + 1);
        }

        scriptMapper.updateById(script);
        log.info("更新脚本成功，脚本ID: {}, 版本: {}", script.getId(), script.getVersion());
    }

    /**
     * 删除脚本（逻辑删除）
     *
     * @param scriptId 脚本ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteScript(Long scriptId) {
        ExcelParseScript script = scriptMapper.selectById(scriptId);
        if (script == null) {
            throw new RuntimeException("脚本不存在，ID: " + scriptId);
        }

        script.setDeleted(1);
        scriptMapper.updateById(script);

        // 清除缓存
        scriptEngine.removeCache(script.getScriptContent());

        log.info("删除脚本成功，脚本ID: {}", scriptId);
    }

    /**
     * 测试脚本
     *
     * @param scriptId 脚本ID
     * @param testData 测试数据
     * @return 测试结果
     */
    public Map<String, Object> testScript(Long scriptId, Map<String, Object> testData) {
        ExcelParseScript script = scriptMapper.selectById(scriptId);
        if (script == null) {
            throw new RuntimeException("脚本不存在，ID: " + scriptId);
        }

        return scriptEngine.testScript(script.getScriptContent(), testData);
    }

    /**
     * 启用/禁用脚本
     *
     * @param scriptId 脚本ID
     * @param status   状态：0-禁用, 1-启用
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long scriptId, Integer status) {
        ExcelParseScript script = scriptMapper.selectById(scriptId);
        if (script == null) {
            throw new RuntimeException("脚本不存在，ID: " + scriptId);
        }

        script.setStatus(status);
        scriptMapper.updateById(script);

        log.info("更新脚本状态成功，脚本ID: {}, 状态: {}", scriptId, status);
    }
}
