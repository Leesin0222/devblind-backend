package com.yongjincompany.devblind.dto;

public record TossRefundWebhookRequest(
        String paymentKey,
        String orderId,
        String status,
        String cancelReason,
        Long cancelAmount
) {}
