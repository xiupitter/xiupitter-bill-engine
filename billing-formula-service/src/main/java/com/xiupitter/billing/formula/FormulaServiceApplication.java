package com.xiupitter.billing.formula;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 计费公式服务启动类
 *
 * @author xiupitter
 */
@EnableCaching
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.xiupitter.billing")
public class FormulaServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FormulaServiceApplication.class, args);
    }
}
