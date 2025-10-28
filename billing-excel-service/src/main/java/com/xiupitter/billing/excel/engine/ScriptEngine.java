package com.xiupitter.billing.excel.engine;

import lombok.extern.slf4j.Slf4j;
import org.mvel2.MVEL;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 脚本执行引擎
 * 基于MVEL实现动态脚本执行
 *
 * @author xiupitter
 */
@Slf4j
@Component
public class ScriptEngine {

    /**
     * 脚本编译缓存
     * key: 脚本内容的hash值
     * value: 编译后的脚本对象
     */
    private final Map<String, Serializable> scriptCache = new ConcurrentHashMap<>();

    /**
     * 执行MVEL脚本
     *
     * @param scriptContent 脚本内容
     * @param context       上下文变量
     * @return 脚本执行结果
     */
    public Object executeScript(String scriptContent, Map<String, Object> context) {
        try {
            // 获取编译后的脚本
            Serializable compiled = getCompiledScript(scriptContent);

            // 执行脚本
            Object result = MVEL.executeExpression(compiled, context);

            log.debug("脚本执行成功，结果: {}", result);
            return result;

        } catch (Exception e) {
            log.error("脚本执行失败，脚本内容: {}", scriptContent, e);
            throw new RuntimeException("脚本执行失败: " + e.getMessage(), e);
        }
    }

    /**
     * 执行解析函数脚本
     * 用于Excel行数据解析
     *
     * @param scriptContent 脚本内容（应返回一个解析函数）
     * @param rowData       行数据
     * @return 解析结果
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> executeParseScript(String scriptContent, Map<Integer, Object> rowData) {
        try {
            // 1. 编译并执行脚本，获取解析函数
            Serializable compiled = getCompiledScript(scriptContent);
            Object parseFunction = MVEL.executeExpression(compiled);

            log.debug("脚本编译成功，解析函数: {}", parseFunction);

            // 2. 准备上下文，将行数据和解析函数放入上下文
            Map<String, Object> context = new ConcurrentHashMap<>();
            context.put("row", rowData);
            context.put("parseRow", parseFunction);

            // 3. 调用解析函数
            String invokeScript = "parseRow(row)";
            Serializable invokeCompiled = MVEL.compileExpression(invokeScript);
            Object result = MVEL.executeExpression(invokeCompiled, context);

            log.debug("行数据解析成功，结果: {}", result);

            return (Map<String, Object>) result;

        } catch (Exception e) {
            log.error("解析脚本执行失败，行数据: {}", rowData, e);
            throw new RuntimeException("解析脚本执行失败: " + e.getMessage(), e);
        }
    }

    /**
     * 测试脚本是否可以正常执行
     *
     * @param scriptContent 脚本内容
     * @param testData      测试数据
     * @return 测试结果
     */
    public Map<String, Object> testScript(String scriptContent, Map<String, Object> testData) {
        try {
            Object result = executeScript(scriptContent, testData);

            Map<String, Object> testResult = new ConcurrentHashMap<>();
            testResult.put("success", true);
            testResult.put("result", result);
            testResult.put("message", "脚本测试通过");

            return testResult;

        } catch (Exception e) {
            log.error("脚本测试失败", e);

            Map<String, Object> testResult = new ConcurrentHashMap<>();
            testResult.put("success", false);
            testResult.put("error", e.getMessage());
            testResult.put("message", "脚本测试失败");

            return testResult;
        }
    }

    /**
     * 获取编译后的脚本（带缓存）
     *
     * @param scriptContent 脚本内容
     * @return 编译后的脚本对象
     */
    private Serializable getCompiledScript(String scriptContent) {
        // 计算脚本内容的hash值作为缓存key
        String cacheKey = String.valueOf(scriptContent.hashCode());

        // 尝试从缓存获取
        return scriptCache.computeIfAbsent(cacheKey, k -> {
            log.debug("编译脚本并加入缓存，key: {}", k);
            return MVEL.compileExpression(scriptContent);
        });
    }

    /**
     * 清空脚本缓存
     */
    public void clearCache() {
        scriptCache.clear();
        log.info("脚本缓存已清空");
    }

    /**
     * 移除指定脚本的缓存
     *
     * @param scriptContent 脚本内容
     */
    public void removeCache(String scriptContent) {
        String cacheKey = String.valueOf(scriptContent.hashCode());
        scriptCache.remove(cacheKey);
        log.info("移除脚本缓存，key: {}", cacheKey);
    }

    /**
     * 获取缓存大小
     *
     * @return 缓存数量
     */
    public int getCacheSize() {
        return scriptCache.size();
    }
}
