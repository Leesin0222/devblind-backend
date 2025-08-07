package com.yongjincompany.devblind.controller;

import com.yongjincompany.devblind.dto.TossWebhookRequest;
import com.yongjincompany.devblind.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhooks/toss")
@RequiredArgsConstructor
public class TossWebhookController {

    private final PaymentService paymentService;

    @PostMapping("/payment")
    public ResponseEntity<Void> receivePaymentWebhook(@RequestBody TossWebhookRequest request) {
        paymentService.handlePaymentResult(request);
        return ResponseEntity.ok().build();
    }
}
