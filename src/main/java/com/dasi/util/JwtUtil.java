package com.dasi.util;

import com.dasi.pojo.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
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
        long exp = now + jwtProperties.getTokenTtl();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(exp))
                .signWith(key, jwtProperties.getSignatureAlgorithm())
                .compact();
    }

    // 解析 Token
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
