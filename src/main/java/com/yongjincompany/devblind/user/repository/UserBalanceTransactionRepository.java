package com.yongjincompany.devblind.user.repository;

import com.yongjincompany.devblind.user.entity.User;
import com.yongjincompany.devblind.user.entity.UserBalanceTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBalanceTransactionRepository extends JpaRepository<UserBalanceTransaction, Long> {
    
    List<UserBalanceTransaction> findByUserIdOrderByCreatedAtDesc(Long userId);
}
