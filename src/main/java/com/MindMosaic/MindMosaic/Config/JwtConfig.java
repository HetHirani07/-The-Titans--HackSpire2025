package com.MindMosaic.MindMosaic.Config;

import org.springframework.beans.factory.annotation.Value;

public class JwtConfig {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    public String getSecret() {
        return secret;
    }

    public long getExpiration() {
        return expiration;
    }
}
