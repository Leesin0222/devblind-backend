package com.yongjincompany.devblind.payment.dto;

public record PaymentProductResponse(
    Long id,
    String name,
    Long price,
    Long coinAmount,
    String description
) {
    public static PaymentProductResponse from(com.yongjincompany.devblind.payment.entity.PaymentProduct product) {
        return new PaymentProductResponse(
            product.getId(),
            product.getName(),
            product.getPrice(),
            product.getCoinAmount(),
            product.getDescription()
        );
    }
}
