package com.yongjincompany.devblind.dto;

public record RefundRequest(
        String orderId,
        String reason
) {}
