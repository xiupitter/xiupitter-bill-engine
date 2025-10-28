package com.xiupitter.billing.engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 计费引擎应用启动类（单体应用）
 *
 * @author xiupitter
 */
@EnableCaching
@SpringBootApplication(scanBasePackages = "com.xiupitter.billing")
public class BillingEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(BillingEngineApplication.class, args);
    }
}
