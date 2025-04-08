package com.xinghuo.pro_classify.config;

import com.xinghuo.pro_classify.properties.MinioProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MinioProperties.class)
@ConditionalOnProperty("minio.endpoint")
public class MinioConfiguration {

}
