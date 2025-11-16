package com.dasi.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties("dasi.wecom")
public class WeComProperties {
    private String corpId;
    private String agentId;
    private String secret;
}
