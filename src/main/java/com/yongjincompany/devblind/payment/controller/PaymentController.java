package com.yongjincompany.devblind.payment.controller;

import com.yongjincompany.devblind.payment.dto.PaymentRequest;
import com.yongjincompany.devblind.payment.dto.PaymentResponse;
import com.yongjincompany.devblind.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Tag(name = "결제", description = "결제 관련 API")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> requestPayment(
            @RequestBody @Valid PaymentRequest request,
            @AuthenticationPrincipal Long userId // JWT 필터에서 유저 ID 추출
    ) {
        return ResponseEntity.ok(paymentService.requestPayment(request, userId));
    }
}

