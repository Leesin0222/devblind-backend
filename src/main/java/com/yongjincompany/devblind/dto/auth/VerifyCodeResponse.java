package com.yongjincompany.devblind.dto.auth;

public record VerifyCodeResponse(
        boolean isRegistered,
        AuthResponse authResponse,    // isRegistered == true 일 때 access/refresh 토큰 반환
        String signupToken            // isRegistered == false 일 때 회원가입용 토큰 반환
) {}