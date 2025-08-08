package com.yongjincompany.devblind.repository;

import com.yongjincompany.devblind.entity.DeviceToken;
import com.yongjincompany.devblind.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {
    Optional<DeviceToken> findByToken(String token);
    List<DeviceToken> findAllByUser(User user);
    Optional<DeviceToken> findByUserId(Long userId);
}
