package com.yongjincompany.devblind.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public record RefundHistoryResponse(
    Long id,
    String paymentId,
    Long amount,
    String reason,
    String status,
    LocalDateTime refundedAt,
    String productName
) {
    public static RefundHistoryResponse from(com.yongjincompany.devblind.payment.entity.PaymentHistory history, String productName) {
        return new RefundHistoryResponse(
            history.getId(),
            history.getPaymentId(),
            history.getAmount(),
            history.getRefundReason(),
            history.getRefundStatus().name(),
            history.getRefundedAt(),
            productName
        );
    }
}
