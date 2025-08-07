package com.yongjincompany.devblind.service;

import com.yongjincompany.devblind.common.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtProperties jwtProperties;

    private static final String REFRESH_PREFIX = "refresh:";

    public void saveRefreshToken(Long userId, String refreshToken) {
        String key = REFRESH_PREFIX + userId;
        long expirationMillis = jwtProperties.getRefreshExpiration();
        redisTemplate.opsForValue().set(key, refreshToken, Duration.ofMillis(expirationMillis));
    }

    public boolean isRefreshTokenValid(Long userId, String token) {
        String key = REFRESH_PREFIX + userId;
        String stored = redisTemplate.opsForValue().get(key);
        return stored != null && stored.equals(token);
    }

    public void deleteRefreshToken(Long userId) {
        redisTemplate.delete(REFRESH_PREFIX + userId);
    }
}
