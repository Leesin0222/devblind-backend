package com.yongjincompany.devblind.service;

import com.yongjincompany.devblind.entity.User;
import com.yongjincompany.devblind.entity.UserBalance;
import com.yongjincompany.devblind.entity.UserBalanceTransaction;
import com.yongjincompany.devblind.repository.UserBalanceRepository;
import com.yongjincompany.devblind.repository.UserBalanceTransactionRepository;
import com.yongjincompany.devblind.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserBalanceService {

    private final UserRepository userRepository;
    private final UserBalanceRepository balanceRepository;
    private final UserBalanceTransactionRepository transactionRepository;

    @Transactional
    public void charge(Long userId, long amount) {
        UserBalance balance = balanceRepository.findById(userId)
                .orElseGet(() -> UserBalance.builder()
                        .userId(userId)
                        .balance(0L)
                        .build());

        balance.charge(amount);
        balanceRepository.save(balance);

        transactionRepository.save(UserBalanceTransaction.builder()
                .userId(userId)
                .type(UserBalanceTransaction.TransactionType.CHARGE)
                .amount(amount)
                .balanceAfter(balance.getBalance())
                .build());
    }

    @Transactional
    public void spend(Long userId, long amount) {
        UserBalance balance = balanceRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("잔액이 없습니다."));

        balance.spend(amount);
        balanceRepository.save(balance);

        transactionRepository.save(UserBalanceTransaction.builder()
                .userId(userId)
                .type(UserBalanceTransaction.TransactionType.SPEND)
                .amount(amount)
                .balanceAfter(balance.getBalance())
                .build());
    }

    public long getBalance(Long userId) {
        return balanceRepository.findById(userId)
                .map(UserBalance::getBalance)
                .orElse(0L);
    }

    public List<UserBalanceTransaction> getTransactions(Long userId) {
        return transactionRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public void deduct(Long userId, Long amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getBalance() < amount) {
            throw new RuntimeException("잔액 부족");
        }

        user.deductBalance(amount);
    }


}

