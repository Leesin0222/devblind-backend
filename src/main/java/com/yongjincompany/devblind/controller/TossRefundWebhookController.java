package com.yongjincompany.devblind.controller;

import com.yongjincompany.devblind.dto.TossRefundWebhookRequest;
import com.yongjincompany.devblind.service.RefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhooks/toss/refund")
@RequiredArgsConstructor
public class TossRefundWebhookController {

    private final RefundService refundService;

    @PostMapping
    public ResponseEntity<Void> receiveRefundWebhook(@RequestBody TossRefundWebhookRequest request) {
        refundService.handleRefundResult(request);
        return ResponseEntity.ok().build();
    }
}
