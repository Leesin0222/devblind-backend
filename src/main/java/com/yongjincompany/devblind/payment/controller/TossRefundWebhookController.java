package com.yongjincompany.devblind.controller;

import com.yongjincompany.devblind.payment.dto.TossRefundWebhookRequest;
import com.yongjincompany.devblind.payment.service.RefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/webhooks/toss-refund")
@RequiredArgsConstructor
@Tag(name = "Toss 환불 웹훅", description = "Toss 환불 웹훅 API")
public class TossRefundWebhookController {

    private final RefundService refundService;

    @PostMapping
    public ResponseEntity<Void> handleTossRefundWebhook(@RequestBody TossRefundWebhookRequest request) {
        refundService.handleTossRefundWebhook(request);
        return ResponseEntity.ok().build();
    }
}
