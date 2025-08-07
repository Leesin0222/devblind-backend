package com.yongjincompany.devblind.controller;

import com.yongjincompany.devblind.dto.PaymentProductResponse;
import com.yongjincompany.devblind.service.PaymentProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/payment/products")
@RequiredArgsConstructor
public class PaymentProductController {

    private final PaymentProductService productService;

    @GetMapping
    public ResponseEntity<List<PaymentProductResponse>> getProducts() {
        return ResponseEntity.ok(productService.getActiveProducts());
    }
}
