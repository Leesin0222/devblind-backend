package com.yongjincompany.devblind.controller;

import com.yongjincompany.devblind.common.AuthUser;
import com.yongjincompany.devblind.dto.PaymentHistoryResponse;
import com.yongjincompany.devblind.service.PaymentHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/payment-histories")
@RequiredArgsConstructor
@Tag(name = "결제 히스토리", description = "결제 내역 조회 API")
public class PaymentHistoryController {

    private final PaymentHistoryService paymentHistoryService;

    @GetMapping
    public ResponseEntity<List<PaymentHistoryResponse>> getPaymentHistories(@AuthUser Long userId) {
        return ResponseEntity.ok(paymentHistoryService.getPaymentHistories(userId));
    }
}
