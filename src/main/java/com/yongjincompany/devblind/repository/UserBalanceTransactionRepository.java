package com.yongjincompany.devblind.repository;

import com.yongjincompany.devblind.entity.UserBalanceTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserBalanceTransactionRepository extends JpaRepository<UserBalanceTransaction, Long> {
    List<UserBalanceTransaction> findByUserIdOrderByCreatedAtDesc(Long userId);
}
