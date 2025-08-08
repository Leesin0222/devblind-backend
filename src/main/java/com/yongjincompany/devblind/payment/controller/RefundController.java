package com.yongjincompany.devblind.controller;

import com.yongjincompany.devblind.common.AuthUser;
import com.yongjincompany.devblind.dto.RefundRequest;
import com.yongjincompany.devblind.service.RefundService;
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
        refundService.requestRefund(userId, request);
        return ResponseEntity.ok().build();
    }
}
