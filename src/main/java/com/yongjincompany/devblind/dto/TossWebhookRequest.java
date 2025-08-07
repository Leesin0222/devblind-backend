package com.yongjincompany.devblind.dto;

public record TossWebhookRequest(
        String paymentKey,
        String orderId,
        String status,
        Long amount
) {}