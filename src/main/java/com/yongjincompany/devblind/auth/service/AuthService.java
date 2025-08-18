package com.yongjincompany.devblind.auth.service;

import com.yongjincompany.devblind.common.security.JwtProvider;
import com.yongjincompany.devblind.auth.dto.AuthResponse;
import com.yongjincompany.devblind.auth.dto.TokenRefreshRequest;
import com.yongjincompany.devblind.auth.dto.TokenRefreshResponse;
import com.yongjincompany.devblind.auth.dto.VerifyCodeResponse;
import com.yongjincompany.devblind.user.entity.User;
import com.yongjincompany.devblind.common.exception.ApiException;
import com.yongjincompany.devblind.common.exception.ErrorCode;
import com.yongjincompany.devblind.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;


    public TokenRefreshResponse refreshAccessToken(TokenRefreshRequest request) {
        String refreshToken = request.refreshToken();

        // 1. Refresh Token 유효성 검증
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new ApiException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 2. 토큰에서 userId 추출
        Long userId = jwtProvider.getUserIdFromToken(refreshToken);

        // 3. Redis에 저장된 토큰과 일치하는지 확인
        if (!refreshTokenService.isRefreshTokenValid(userId, refreshToken)) {
            throw new ApiException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 4. 새로운 AccessToken, RefreshToken 발급
        String newAccessToken = jwtProvider.generateAccessToken(userId);
        String newRefreshToken = jwtProvider.generateRefreshToken(userId);

        // 5. Redis에 새로운 RefreshToken 저장
        refreshTokenService.saveRefreshToken(userId, newRefreshToken);

        return new TokenRefreshResponse(newAccessToken, newRefreshToken);
    }

    @Transactional
    public VerifyCodeResponse verifyCodeAndLoginOrSignup(String phone, String code) {
        String key = "sms:" + phone;
        String storedCode = redisTemplate.opsForValue().get(key);

        if (storedCode == null || !storedCode.equals(code)) {
            throw new ApiException(ErrorCode.INVALID_CODE);
        }

        redisTemplate.delete(key); // 1회성 코드 삭제

        Optional<User> user = userRepository.findByPhoneNumberAndDeletedFalse(phone);
        if (user.isPresent()) {
            Long userId = user.get().getId();

            String accessToken = jwtProvider.generateAccessToken(userId);
            String refreshToken = jwtProvider.generateRefreshToken(userId);

            refreshTokenService.saveRefreshToken(userId, refreshToken);

            AuthResponse authResponse = new AuthResponse(accessToken, refreshToken, user.get().getId(), user.get().getNickname());
            return new VerifyCodeResponse(true, authResponse, null);
        } else {
            String signupToken = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set("signupToken:" + signupToken, phone, Duration.ofMinutes(10));
            return new VerifyCodeResponse(false, null, signupToken);
        }
    }

    public void logout(String accessToken) {
        if (!jwtProvider.validateToken(accessToken)) {
            throw new ApiException(ErrorCode.INVALID_TOKEN);
        }

        Long userId = jwtProvider.getUserIdFromToken(accessToken);
        refreshTokenService.deleteRefreshToken(userId);
    }
}

