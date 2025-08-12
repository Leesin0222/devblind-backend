package com.yongjincompany.devblind.auth.dto;

public record TokenRefreshResponse(
    String accessToken,
    String refreshToken
) {}
