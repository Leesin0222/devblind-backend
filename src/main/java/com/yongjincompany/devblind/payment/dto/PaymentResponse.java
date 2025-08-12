package com.yongjincompany.devblind.payment.dto;

public record PaymentResponse(
    String paymentId,
    String orderId,
    Long amount,
    String paymentUrl,
    String status
) {}