package com.yongjincompany.devblind.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
public class TossConfig {

    @Value("${toss.payment.secret-key}")
    private String secretKey;

    @Bean
    public WebClient tossWebClient() {
        String auth = secretKey + ":";
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        
        return WebClient.builder()
                .defaultHeader("Authorization", "Basic " + encodedAuth)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
