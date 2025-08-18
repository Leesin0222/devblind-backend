package com.yongjincompany.devblind.user.service;

import com.yongjincompany.devblind.user.entity.UserBalance;
import com.yongjincompany.devblind.user.entity.UserBalanceTransaction;
import com.yongjincompany.devblind.common.exception.ApiException;
import com.yongjincompany.devblind.common.exception.ErrorCode;
import com.yongjincompany.devblind.user.repository.UserBalanceRepository;
import com.yongjincompany.devblind.user.repository.UserBalanceTransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserBalanceService {

    private final UserBalanceRepository balanceRepository;
    private final UserBalanceTransactionRepository transactionRepository;

    @Transactional
    public void charge(Long userId, long amount) {
        log.info("잔액 충전 요청: userId={}, amount={}", userId, amount);
        
        UserBalance balance = balanceRepository.findById(userId)
                .orElseGet(() -> {
                    log.debug("새로운 잔액 레코드 생성: userId={}", userId);
                    return UserBalance.builder()
                            .userId(userId)
                            .balance(0L)
                            .build();
                });

        balance.charge(amount);
        balanceRepository.save(balance);

        UserBalanceTransaction transaction = UserBalanceTransaction.builder()
                .userId(userId)
                .type(UserBalanceTransaction.TransactionType.CHARGE)
                .amount(amount)
                .balanceAfter(balance.getBalance())
                .build();
        transactionRepository.save(transaction);

        log.info("잔액 충전 완료: userId={}, amount={}, newBalance={}", userId, amount, balance.getBalance());
    }

    @Transactional
    public void spend(Long userId, long amount) {
        log.info("잔액 사용 요청: userId={}, amount={}", userId, amount);
        
        UserBalance balance = balanceRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("잔액 레코드를 찾을 수 없음: userId={}", userId);
                    return new ApiException(ErrorCode.USER_NOT_FOUND);
                });

        try {
            balance.spend(amount);
            balanceRepository.save(balance);

            UserBalanceTransaction transaction = UserBalanceTransaction.builder()
                    .userId(userId)
                    .type(UserBalanceTransaction.TransactionType.SPEND)
                    .amount(amount)
                    .balanceAfter(balance.getBalance())
                    .build();
            transactionRepository.save(transaction);

            log.info("잔액 사용 완료: userId={}, amount={}, remainingBalance={}", userId, amount, balance.getBalance());
        } catch (IllegalStateException e) {
            log.error("잔액 부족: userId={}, requestedAmount={}, currentBalance={}", userId, amount, balance.getBalance());
            throw new ApiException(ErrorCode.INSUFFICIENT_BALANCE);
        }
    }

    public long getBalance(Long userId) {
        long balance = balanceRepository.findById(userId)
                .map(UserBalance::getBalance)
                .orElse(0L);
        
        log.debug("잔액 조회: userId={}, balance={}", userId, balance);
        return balance;
    }

    public List<UserBalanceTransaction> getTransactions(Long userId) {
        List<UserBalanceTransaction> transactions = transactionRepository.findByUserIdOrderByCreatedAtDesc(userId);
        log.debug("거래 내역 조회: userId={}, transactionCount={}", userId, transactions.size());
        return transactions;
    }

    // User 엔티티의 balance 필드와의 중복을 제거하기 위해 이 메서드는 제거
    // 대신 spend 메서드를 사용하도록 변경
}

