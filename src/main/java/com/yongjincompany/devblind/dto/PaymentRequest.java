package com.yongjincompany.devblind.dto;

public record PaymentRequest(
        Long productId,
        String successUrl,
        String failUrl
) {}