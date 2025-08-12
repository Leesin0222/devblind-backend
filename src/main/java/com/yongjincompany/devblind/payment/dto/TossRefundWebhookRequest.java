package com.yongjincompany.devblind.payment.dto;

public record TossRefundWebhookRequest(
    String orderId,
    String refundId,
    Long amount,
    String status,
    String reason
) {}
