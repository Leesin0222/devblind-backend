package com.yongjincompany.devblind.repository;

import com.yongjincompany.devblind.entity.UserBalance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBalanceRepository extends JpaRepository<UserBalance, Long> {
}
