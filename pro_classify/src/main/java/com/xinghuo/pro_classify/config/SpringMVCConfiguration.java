package com.xinghuo.pro_classify.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * SpringMVC管理类
 */
@EnableTransactionManagement
@Configuration
public class SpringMVCConfiguration implements WebMvcConfigurer {
    /**
     * 开启跨域
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*") //TODO 改为前端ip
                .allowedHeaders("*")
                .allowedMethods("GET", "POST", "OPTIONS", "DELETE", "PUT", "PATCH");
    }
}

