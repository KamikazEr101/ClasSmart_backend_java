package com.xinghuo.pro_classify.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * Mybatis管理类
 */
@Configuration
@MapperScan(basePackages = "com.xinghuo.pro_classify.mapper")
public class MybatisConfiguration {

}
