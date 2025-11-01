package com.dasi.common.properties;

import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "dasi.jwt")
@Data
public class JwtProperties {
    private String secretKey;
    private String tokenName;
    private String claimUserKey;
    private Long tokenTtl;
    private SignatureAlgorithm signatureAlgorithm;
}