package com.yongjincompany.devblind.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public record TossRefundWebhookRequest(
    String orderId,
    String refundId,
    Long amount,
    String status,
    String reason
) {}
