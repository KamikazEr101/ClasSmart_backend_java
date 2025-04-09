package com.xinghuo.pro_classify.config;

import com.xinghuo.pro_classify.properties.PythonBackendsProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 访问python后端参数配置类
 */
@Configuration
@EnableConfigurationProperties(PythonBackendsProperties.class)
public class PythonBackendsConfiguration {
}
