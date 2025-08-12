package com.yongjincompany.devblind.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RefundRequest(
    @NotBlank(message = "주문 ID는 필수입니다")
    String orderId,
    
    @NotNull(message = "환불 금액은 필수입니다")
    Long amount,
    
    String reason
) {}
