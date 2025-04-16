package com.xinghuo.pro_classify.config;

import com.xinghuo.pro_classify.properties.LimitProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(LimitProperties.class)
public class LimitConfiguration {

}
