package com.yongjincompany.devblind.payment.controller;

import com.yongjincompany.devblind.common.security.AuthUser;
import com.yongjincompany.devblind.payment.dto.RefundRequest;
import com.yongjincompany.devblind.payment.service.RefundService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/refunds")
@RequiredArgsConstructor
@Tag(name = "환불", description = "환불 요청 API")
public class RefundController {

    private final RefundService refundService;

    @PostMapping
    public ResponseEntity<Void> requestRefund(
            @AuthUser Long userId,
            @RequestBody @Valid RefundRequest request
    ) {
        refundService.requestRefund(request, userId);
        return ResponseEntity.ok().build();
    }
}
