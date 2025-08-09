package com.yongjincompany.devblind.auth.controller;

import com.yongjincompany.devblind.auth.dto.AuthResponse;
import com.yongjincompany.devblind.auth.dto.SignupRequest;
import com.yongjincompany.devblind.auth.dto.SmsSendRequest;
import com.yongjincompany.devblind.auth.dto.SmsVerifyRequest;
import com.yongjincompany.devblind.auth.dto.TokenRefreshRequest;
import com.yongjincompany.devblind.auth.dto.TokenRefreshResponse;
import com.yongjincompany.devblind.auth.dto.VerifyCodeResponse;
import com.yongjincompany.devblind.common.exception.ApiException;
import com.yongjincompany.devblind.common.exception.ErrorCode;
import com.yongjincompany.devblind.auth.service.AuthService;
import com.yongjincompany.devblind.auth.service.SignupService;
import com.yongjincompany.devblind.auth.service.SmsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/auth/sms")
@RequiredArgsConstructor
@Tag(name = "인증", description = "SMS 인증 및 로그인 API")
public class AuthController {

    private final SmsService smsService;
    private final AuthService authService;
    private final SignupService signupService;

    @PostMapping("/send")
    public ResponseEntity<Void> sendCode(@RequestBody @Valid SmsSendRequest request) {
        smsService.sendVerificationCode(request.phoneNumber());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify")
    public ResponseEntity<VerifyCodeResponse> verifyCode(@RequestBody @Valid SmsVerifyRequest request) {
        return ResponseEntity.ok(authService.verifyCodeAndLoginOrSignup(request.phoneNumber(), request.code()));
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody @Valid SignupRequest request) {
        AuthResponse authResponse = signupService.signup(request);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@RequestBody TokenRefreshRequest request) {
        TokenRefreshResponse response = authService.refreshAccessToken(request);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authorizationHeader) {
        // Authorization: Bearer {accessToken}
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }

        String accessToken = authorizationHeader.substring(7);
        authService.logout(accessToken);

        return ResponseEntity.ok().build();
    }

}