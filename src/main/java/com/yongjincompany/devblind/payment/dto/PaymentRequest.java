package com.yongjincompany.devblind.payment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public record PaymentRequest(
    @NotNull(message = "상품 ID는 필수입니다")
    Long productId,
    
    @NotNull(message = "결제 방법은 필수입니다")
    String paymentMethod, // CARD, TRANSFER, VIRTUAL_ACCOUNT
    
    String successUrl,
    String failUrl
) {}