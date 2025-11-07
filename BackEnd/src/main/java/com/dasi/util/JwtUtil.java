package com.dasi.util;

import com.dasi.common.enumeration.ResultInfo;
import com.dasi.common.exception.JwtErrorException;
import com.dasi.common.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
@Slf4j
public class JwtUtil {
    @Autowired
    private JwtProperties jwtProperties;

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
    }

    // 生成 Token
    public String createToken(Map<String, Object> claims) {
        long now = System.currentTimeMillis();
        long exp = now + jwtProperties.getTtl();

        return Jwts.builder()
                .addClaims(claims)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(exp))
                .signWith(key, jwtProperties.getSignatureAlgorithm())
                .compact();
    }

    // 解析 Token
    public Claims parseToken(String token) {
        try {
            if (token == null || token.isBlank()) {
                throw new JwtErrorException(ResultInfo.TOKEN_MISSING);
            }

            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("JWT 校验失败：{}", e.getMessage());
            throw new JwtErrorException(ResultInfo.TOKEN_EXPIRED);
        } catch (JwtException exception) {
            log.error("JWT 校验失败：{}", exception.getMessage());
            throw new JwtErrorException(ResultInfo.TOKEN_INVALID);
        }
    }

    // 校验 Token
    public String refreshToken(String token) {
        Claims claims = parseToken(token);
        claims.remove("iat");
        claims.remove("exp");
        return createToken(claims);
    }
}
