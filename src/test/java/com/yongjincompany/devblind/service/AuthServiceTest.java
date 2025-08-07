package com.yongjincompany.devblind.service;

import com.yongjincompany.devblind.common.JwtProvider;
import com.yongjincompany.devblind.dto.auth.TokenRefreshRequest;
import com.yongjincompany.devblind.dto.auth.TokenRefreshResponse;
import com.yongjincompany.devblind.dto.auth.VerifyCodeResponse;
import com.yongjincompany.devblind.entity.User;
import com.yongjincompany.devblind.exception.ApiException;
import com.yongjincompany.devblind.exception.ErrorCode;
import com.yongjincompany.devblind.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthService authService;

    @Test
    void refreshAccessToken_whenRefreshTokenInvalid_thenThrow() {
        String invalidToken = "invalid-refresh-token";
        TokenRefreshRequest request = new TokenRefreshRequest(invalidToken);

        when(jwtProvider.validateToken(invalidToken)).thenReturn(false);

        ApiException ex = assertThrows(ApiException.class, () -> {
            authService.refreshAccessToken(request);
        });

        assertEquals(ErrorCode.INVALID_REFRESH_TOKEN, ex.getErrorCode());
    }

    @Test
    void refreshAccessToken_whenRefreshTokenNotMatchInRedis_thenThrow() {
        String refreshToken = "valid-refresh-token";
        TokenRefreshRequest request = new TokenRefreshRequest(refreshToken);

        when(jwtProvider.validateToken(refreshToken)).thenReturn(true);
        when(jwtProvider.getUserIdFromToken(refreshToken)).thenReturn(1L);
        when(refreshTokenService.isRefreshTokenValid(1L, refreshToken)).thenReturn(false);

        ApiException ex = assertThrows(ApiException.class, () -> {
            authService.refreshAccessToken(request);
        });

        assertEquals(ErrorCode.INVALID_REFRESH_TOKEN, ex.getErrorCode());
    }

    @Test
    void refreshAccessToken_whenValidToken_thenReturnNewTokens() {
        String oldRefreshToken = "old-refresh-token";
        TokenRefreshRequest request = new TokenRefreshRequest(oldRefreshToken);

        when(jwtProvider.validateToken(oldRefreshToken)).thenReturn(true);
        when(jwtProvider.getUserIdFromToken(oldRefreshToken)).thenReturn(1L);
        when(refreshTokenService.isRefreshTokenValid(1L, oldRefreshToken)).thenReturn(true);

        when(jwtProvider.generateAccessToken(1L)).thenReturn("new-access-token");
        when(jwtProvider.generateRefreshToken(1L)).thenReturn("new-refresh-token");

        doNothing().when(refreshTokenService).saveRefreshToken(1L, "new-refresh-token");

        TokenRefreshResponse response = authService.refreshAccessToken(request);

        assertEquals("new-access-token", response.getAccessToken());
        assertEquals("new-refresh-token", response.getRefreshToken());
        verify(refreshTokenService, times(1)).saveRefreshToken(1L, "new-refresh-token");
    }

    @Test
    void verifyCodeAndLoginOrSignup_whenCodeInvalid_thenThrow() {
        String phone = "01012345678";
        String code = "123456";

        when(redisTemplate.opsForValue().get("sms:" + phone)).thenReturn("654321");

        ApiException ex = assertThrows(ApiException.class, () -> {
            authService.verifyCodeAndLoginOrSignup(phone, code);
        });

        assertEquals(ErrorCode.INVALID_CODE, ex.getErrorCode());
    }

    @Test
    void verifyCodeAndLoginOrSignup_whenUserExists_thenReturnAuthResponse() {
        String phone = "01012345678";
        String code = "123456";

        User user = User.builder().id(1L).phoneNumber(phone).build();

        when(redisTemplate.opsForValue().get("sms:" + phone)).thenReturn(code);
        when(userRepository.findByPhoneNumberAndDeletedFalse(phone)).thenReturn(Optional.of(user));
        when(jwtProvider.generateAccessToken(1L)).thenReturn("access-token");
        when(jwtProvider.generateRefreshToken(1L)).thenReturn("refresh-token");

        doNothing().when(refreshTokenService).saveRefreshToken(1L, "refresh-token");

        VerifyCodeResponse response = authService.verifyCodeAndLoginOrSignup(phone, code);

        assertTrue(response.isRegistered());
        assertNotNull(response.authResponse());
        assertEquals("access-token", response.authResponse().accessToken());
        assertEquals("refresh-token", response.authResponse().refreshToken());

        verify(redisTemplate, times(1)).delete("sms:" + phone);
        verify(refreshTokenService, times(1)).saveRefreshToken(1L, "refresh-token");
    }

    @Test
    void verifyCodeAndLoginOrSignup_whenUserNotExists_thenReturnSignupToken() {
        String phone = "01012345678";
        String code = "123456";

        when(redisTemplate.opsForValue().get("sms:" + phone)).thenReturn(code);
        when(userRepository.findByPhoneNumberAndDeletedFalse(phone)).thenReturn(Optional.empty());

        doNothing().when(redisTemplate.opsForValue()).set(anyString(), anyString(), any());

        VerifyCodeResponse response = authService.verifyCodeAndLoginOrSignup(phone, code);

        assertFalse(response.isRegistered());
        assertNull(response.authResponse());
        assertNotNull(response.signupToken());

        verify(redisTemplate, times(1)).delete("sms:" + phone);
        verify(redisTemplate.opsForValue(), times(1))
                .set(startsWith("signupToken:"), eq(phone), any(Duration.class));
    }

    @Test
    void logout_whenInvalidToken_thenThrow() {
        String invalidToken = "invalid-token";

        when(jwtProvider.validateToken(invalidToken)).thenReturn(false);

        ApiException ex = assertThrows(ApiException.class, () -> {
            authService.logout(invalidToken);
        });

        assertEquals(ErrorCode.INVALID_TOKEN, ex.getErrorCode());
    }

    @Test
    void logout_whenValidToken_thenDeleteRefreshToken() {
        String accessToken = "valid-access-token";

        when(jwtProvider.validateToken(accessToken)).thenReturn(true);
        when(jwtProvider.getUserIdFromToken(accessToken)).thenReturn(1L);

        doNothing().when(refreshTokenService).deleteRefreshToken(1L);

        authService.logout(accessToken);

        verify(refreshTokenService, times(1)).deleteRefreshToken(1L);
    }
}
