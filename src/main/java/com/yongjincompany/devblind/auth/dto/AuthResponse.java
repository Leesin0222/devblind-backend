package com.yongjincompany.devblind.auth.dto;

public record AuthResponse(
    String accessToken,
    String refreshToken,
    Long userId,
    String nickname
) {}