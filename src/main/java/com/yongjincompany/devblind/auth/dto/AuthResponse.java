package com.yongjincompany.devblind.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public record AuthResponse(
    String accessToken,
    String refreshToken,
    Long userId,
    String nickname
) {}