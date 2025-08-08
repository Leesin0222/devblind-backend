package com.yongjincompany.devblind.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
public class TossConfig {

    @Value("${toss.secret-key}")
    private String secretKey;

    @Bean
    public RestTemplate tossRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // 토스 API 인증용 Basic Auth 헤더 세팅 인터셉터 추가
        restTemplate.getInterceptors().add((request, body, execution) -> {
            String auth = secretKey + ":";
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
            request.getHeaders().add("Authorization", "Basic " + encodedAuth);
            request.getHeaders().add("Content-Type", "application/json");
            return execution.execute(request, body);
        });

        return restTemplate;
    }
}
