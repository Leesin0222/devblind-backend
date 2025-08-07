package com.yongjincompany.devblind.controller;

import com.yongjincompany.devblind.dto.auth.*;
import com.yongjincompany.devblind.exception.ApiException;
import com.yongjincompany.devblind.exception.ErrorCode;
import com.yongjincompany.devblind.service.AuthService;
import com.yongjincompany.devblind.service.SignupService;
import com.yongjincompany.devblind.service.SmsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/sms")
@RequiredArgsConstructor
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