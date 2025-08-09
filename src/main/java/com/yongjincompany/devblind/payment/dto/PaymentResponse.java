package com.yongjincompany.devblind.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public record PaymentResponse(
    String paymentId,
    String orderId,
    Long amount,
    String paymentUrl,
    String status
) {}