package com.yongjincompany.devblind.controller;

import com.yongjincompany.devblind.common.security.AuthUser;
import com.yongjincompany.devblind.payment.dto.RefundHistoryResponse;
import com.yongjincompany.devblind.payment.service.RefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/refund-histories")
@RequiredArgsConstructor
@Tag(name = "환불 히스토리", description = "환불 내역 조회 API")
public class RefundHistoryController {

    private final RefundService refundService;

    @GetMapping
    public ResponseEntity<List<RefundHistoryResponse>> getRefundHistories(@AuthUser Long userId) {
        return ResponseEntity.ok(refundService.getRefundHistories(userId));
    }
}
