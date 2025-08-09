package com.yongjincompany.devblind.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public record TokenRefreshRequest(
    @NotBlank(message = "리프레시 토큰은 필수입니다")
    String refreshToken
) {}