package com.yongjincompany.devblind.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class TossPaymentClient {

    private final WebClient webClient;

    @Value("${toss.payment.secret-key}")
    private String secretKey;
    private static final String PAYMENT_URL = "https://api.tosspayments.com/v1/payments";

    public String requestPayment(String orderId, String orderName, Long amount, String successUrl, String failUrl) {
        Map<String, Object> body = Map.of(
                "amount", amount,
                "orderId", orderId,
                "orderName", orderName,
                "successUrl", successUrl,
                "failUrl", failUrl
        );

        Map<String, Object> response = webClient.post()
                .uri(PAYMENT_URL)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return (String) response.get("nextRedirectUrl");
    }
}
