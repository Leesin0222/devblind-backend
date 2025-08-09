package com.yongjincompany.devblind.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public record PaymentHistoryResponse(
    Long id,
    String paymentId,
    String productName,
    Long amount,
    String status,
    LocalDateTime paidAt,
    String refundStatus
) {
    public static PaymentHistoryResponse from(com.yongjincompany.devblind.payment.entity.PaymentHistory history, String productName) {
        return new PaymentHistoryResponse(
            history.getId(),
            history.getPaymentId(),
            productName,
            history.getAmount(),
            history.getStatus().name(),
            history.getPaidAt(),
            history.getRefundStatus().name()
        );
    }
}
