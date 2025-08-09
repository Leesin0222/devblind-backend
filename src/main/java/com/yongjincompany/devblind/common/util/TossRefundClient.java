package com.yongjincompany.devblind.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
public class TossRefundClient {

    private final RestTemplate restTemplate;
    @Value("${toss.refund.secret-key}")
    private String secretKey;
    private static final String REFUND_API_URL = "https://api.tosspayments.com/v1/payments/{paymentKey}/cancel";

    public boolean refund(String paymentKey, Long amount, String reason) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(Base64.getEncoder().encodeToString((secretKey + ":").getBytes()));
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "cancelReason", reason,
                "cancelAmount", amount
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(REFUND_API_URL, request, Map.class, paymentKey);

        return response.getStatusCode().is2xxSuccessful();
    }
}
