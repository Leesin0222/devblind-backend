package com.yongjincompany.devblind.controller;

import com.yongjincompany.devblind.dto.RefundRequest;
import com.yongjincompany.devblind.service.RefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments/refund")
@RequiredArgsConstructor
public class RefundController {

    private final RefundService refundService;

    @PostMapping
    public ResponseEntity<Void> requestRefund(
            @RequestBody RefundRequest request,
            @AuthenticationPrincipal Long userId
    ) {
        refundService.requestRefund(request, userId);
        return ResponseEntity.ok().build();
    }
}
