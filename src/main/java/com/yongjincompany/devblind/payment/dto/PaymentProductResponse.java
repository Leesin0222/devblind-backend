package com.yongjincompany.devblind.dto;

import com.yongjincompany.devblind.entity.PaymentProduct;

public record PaymentProductResponse(
        Long id,
        String name,
        Long amount,
        Long coin
) {
    public static PaymentProductResponse from(PaymentProduct product) {
        return new PaymentProductResponse(
                product.getId(),
                product.getName(),
                product.getAmount(),
                product.getCoin()
        );
    }
}
