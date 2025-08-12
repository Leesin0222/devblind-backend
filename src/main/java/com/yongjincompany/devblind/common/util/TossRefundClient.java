package com.yongjincompany.devblind.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class TossRefundClient {

    private final WebClient webClient;
    @Value("${toss.refund.secret-key}")
    private String secretKey;
    private static final String REFUND_API_URL = "https://api.tosspayments.com/v1/payments/{paymentKey}/cancel";

    public boolean refund(String paymentKey, Long amount, String reason) {
        Map<String, Object> body = Map.of(
                "cancelReason", reason,
                "cancelAmount", amount
        );

        try {
            webClient.post()
                    .uri(REFUND_API_URL, paymentKey)
                    .bodyValue(body)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
