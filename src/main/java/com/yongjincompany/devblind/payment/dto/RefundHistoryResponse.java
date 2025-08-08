package com.yongjincompany.devblind.dto;

import com.yongjincompany.devblind.entity.PaymentHistory;

import java.time.LocalDateTime;

public record RefundHistoryResponse(
        String orderId,
        String productName,
        Long amount,
        Long coin,
        String status,
        LocalDateTime refundRequestedAt,
        LocalDateTime refundCompletedAt
) {
    public static RefundHistoryResponse from(PaymentHistory history, String productName) {
        return new RefundHistoryResponse(
                history.getOrderId(),
                productName,
                history.getAmount(),
                history.getCoin(),
                history.getRefundStatus().name(),
                history.getRefundRequestedAt(),
                history.getRefundCompletedAt()
        );
    }
}
