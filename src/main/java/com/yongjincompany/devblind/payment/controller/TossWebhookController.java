package com.yongjincompany.devblind.payment.controller;

import com.yongjincompany.devblind.payment.dto.TossWebhookRequest;
import com.yongjincompany.devblind.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/webhooks/toss")
@RequiredArgsConstructor
@Tag(name = "Toss 웹훅", description = "Toss 결제 웹훅 API")
public class TossWebhookController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<Void> handleTossWebhook(@RequestBody TossWebhookRequest request) {
        paymentService.handleTossWebhook(request);
        return ResponseEntity.ok().build();
    }
}
