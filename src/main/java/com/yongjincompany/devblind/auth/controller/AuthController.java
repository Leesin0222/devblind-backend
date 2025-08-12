package com.yongjincompany.devblind.auth.controller;

import com.yongjincompany.devblind.auth.dto.*;
import com.yongjincompany.devblind.auth.service.AuthService;
import com.yongjincompany.devblind.auth.service.SignupService;
import com.yongjincompany.devblind.auth.service.SmsService;
import com.yongjincompany.devblind.common.exception.ApiException;
import com.yongjincompany.devblind.common.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/sms")
@RequiredArgsConstructor
@Tag(name = "인증", description = "SMS 인증 및 로그인 API")
public class AuthController {

    private final SmsService smsService;
    private final AuthService authService;
    private final SignupService signupService;

    @Operation(summary = "SMS 인증 코드 발송", description = "휴대폰 번호로 인증 코드를 발송합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 코드 발송 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 휴대폰 번호 형식"),
            @ApiResponse(responseCode = "500", description = "SMS 발송 실패")
    })
    @PostMapping("/send")
    public ResponseEntity<Void> sendCode(@RequestBody @Valid SmsSendRequest request) {
        smsService.sendVerificationCode(request.phoneNumber());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "SMS 인증 코드 검증", description = "인증 코드를 검증하고 로그인하거나 회원가입 토큰을 발급합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 인증 코드"),
            @ApiResponse(responseCode = "401", description = "인증 코드 만료")
    })
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