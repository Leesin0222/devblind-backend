package com.yongjincompany.devblind.common;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TossPaymentClient {

    private final RestTemplate restTemplate;

    private static final String SECRET_KEY = "test_sk_XXXX"; // 토스 시크릿 키
    private static final String PAYMENT_URL = "https://api.tosspayments.com/v1/payments";

    public String requestPayment(String orderId, String orderName, Long amount, String successUrl, String failUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(Base64.getEncoder().encodeToString((SECRET_KEY + ":").getBytes()));
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "amount", amount,
                "orderId", orderId,
                "orderName", orderName,
                "successUrl", successUrl,
                "failUrl", failUrl
        );

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(PAYMENT_URL, requestEntity, Map.class);

        return (String) response.getBody().get("nextRedirectUrl");
    }
}
