package com.yongjincompany.devblind.user.repository;

import com.yongjincompany.devblind.user.entity.UserBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserBalanceRepository extends JpaRepository<UserBalance, Long> {
    
    Optional<UserBalance> findByUserId(Long userId);
}
