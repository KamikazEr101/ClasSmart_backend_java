package com.xinghuo.pro_classify.config;

import com.xinghuo.pro_classify.properties.MinioProperties;
import io.minio.MinioClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = "com.xinghuo.pro_classify.mapper")
public class MybatisConfiguration {
    @Autowired
    private MinioProperties properties;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(properties.getEndPoint())
                .credentials(properties.getAccessKey(),properties.getSecretKey()).
                build();
    }
}
