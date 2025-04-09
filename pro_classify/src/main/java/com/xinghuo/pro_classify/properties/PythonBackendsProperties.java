package com.xinghuo.pro_classify.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * python服务端属性类
 */
@Data
@ConfigurationProperties(prefix = "python")
public class PythonBackendsProperties {
    private String endPoint;
    private String requestPath;
}
