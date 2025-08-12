package com.yongjincompany.devblind.auth.dto;

public record VerifyCodeResponse(
    boolean isRegistered,
    AuthResponse authResponse,
    String signupToken
) {}