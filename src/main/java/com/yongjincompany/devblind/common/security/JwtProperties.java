package com.yongjincompany.devblind.common.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    
    private String secret;
    private long accessTokenExpiration = 3600000; // 1시간
    private long refreshTokenExpiration = 2592000000L; // 30일
}
