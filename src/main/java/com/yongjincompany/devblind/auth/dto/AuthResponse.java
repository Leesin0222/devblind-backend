package com.yongjincompany.devblind.dto.auth;

import lombok.Getter;

public record AuthResponse(
        String accessToken,
        String refreshToken
) {}