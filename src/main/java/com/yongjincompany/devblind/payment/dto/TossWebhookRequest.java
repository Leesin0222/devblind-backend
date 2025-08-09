package com.yongjincompany.devblind.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public record TossWebhookRequest(
    String paymentKey,
    String orderId,
    String status,
    Long totalAmount,
    String transactionKey
) {}