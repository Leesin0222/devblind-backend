package com.yongjincompany.devblind.dto;

import com.yongjincompany.devblind.entity.PaymentHistory;

import java.time.LocalDateTime;

public record PaymentHistoryResponse(
        String orderId,
        String productName,
        Long amount,
        Long coin,
        String status,
        LocalDateTime createdAt
) {
    public static PaymentHistoryResponse from(PaymentHistory history, String productName) {
        return new PaymentHistoryResponse(
                history.getOrderId(),
                productName,
                history.getAmount(),
                history.getCoin(),
                history.getStatus().name(),
                history.getCreatedAt()
        );
    }
}
