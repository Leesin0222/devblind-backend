package com.yongjincompany.devblind.controller;

import com.yongjincompany.devblind.entity.UserBalanceTransaction;
import com.yongjincompany.devblind.service.UserBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/balances")
public class UserBalanceController {

    private final UserBalanceService balanceService;

    @PostMapping("/charge")
    public ResponseEntity<Void> charge(
            @AuthenticationPrincipal Long userId,
            @RequestParam long amount
    ) {
        balanceService.charge(userId, amount);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/spend")
    public ResponseEntity<Void> spend(
            @AuthenticationPrincipal Long userId,
            @RequestParam long amount
    ) {
        balanceService.spend(userId, amount);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Long> getBalance(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(balanceService.getBalance(userId));
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<UserBalanceTransaction>> getTransactions(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(balanceService.getTransactions(userId));
    }
}
