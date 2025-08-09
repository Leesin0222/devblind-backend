package com.yongjincompany.devblind.user.controller;

import com.yongjincompany.devblind.common.security.AuthUser;
import com.yongjincompany.devblind.user.service.UserBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.math.BigDecimal;

@RestController
@RequestMapping("/user-balance")
@RequiredArgsConstructor
@Tag(name = "사용자 잔액", description = "사용자 잔액 조회 API")
public class UserBalanceController {

    private final UserBalanceService userBalanceService;

    @GetMapping
    public ResponseEntity<Long> getUserBalance(@AuthUser Long userId) {
        return ResponseEntity.ok(userBalanceService.getBalance(userId));
    }
}
