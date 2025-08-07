package com.yongjincompany.devblind.controller;

import com.yongjincompany.devblind.dto.RefundHistoryResponse;
import com.yongjincompany.devblind.service.RefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/payments/refund/history")
@RequiredArgsConstructor
public class RefundHistoryController {

    private final RefundService refundService;

    @GetMapping
    public ResponseEntity<List<RefundHistoryResponse>> getRefundHistory(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(refundService.getUserRefundHistories(userId));
    }
}
