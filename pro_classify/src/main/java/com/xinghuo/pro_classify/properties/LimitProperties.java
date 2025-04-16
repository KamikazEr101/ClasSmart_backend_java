package com.xinghuo.pro_classify.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "limit")
public class LimitProperties {
    private final Long ipPermitsPerSecond;
    private final Long blacklistDuration;
    private final double permitsPerSecond;
    private final Long timeout;
}
