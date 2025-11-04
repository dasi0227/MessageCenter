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
    private Long ttl;
    private SignatureAlgorithm signatureAlgorithm;

    private String accountTokenName;
    private String accountIdClaimKey;
    private String accountRoleClaimKey;

    private String contactTokenName;
    private String contactIdClaimKey;
    private String contactInboxClaimKey;
}