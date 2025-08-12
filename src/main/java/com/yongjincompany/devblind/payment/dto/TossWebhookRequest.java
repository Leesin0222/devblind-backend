package com.yongjincompany.devblind.payment.dto;

public record TossWebhookRequest(
    String paymentKey,
    String orderId,
    String status,
    Long totalAmount,
    String transactionKey
) {}