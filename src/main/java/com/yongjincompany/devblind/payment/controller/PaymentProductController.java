package com.yongjincompany.devblind.controller;

import com.yongjincompany.devblind.dto.PaymentProductResponse;
import com.yongjincompany.devblind.service.PaymentProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/payment-products")
@RequiredArgsConstructor
@Tag(name = "결제 상품", description = "결제 상품 조회 API")
public class PaymentProductController {

    private final PaymentProductService paymentProductService;

    @GetMapping
    public ResponseEntity<List<PaymentProductResponse>> getPaymentProducts() {
        return ResponseEntity.ok(paymentProductService.getPaymentProducts());
    }
}
