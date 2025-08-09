package com.yongjincompany.devblind.user.repository;

import com.yongjincompany.devblind.user.entity.DeviceToken;
import com.yongjincompany.devblind.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {
    
    Optional<DeviceToken> findByToken(String token);
    
    List<DeviceToken> findAllByUser(User user);
    
    void deleteByToken(String token);
    
    List<DeviceToken> findByUserId(Long userId);
}
