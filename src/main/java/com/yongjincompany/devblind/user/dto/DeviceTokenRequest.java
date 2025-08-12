package com.yongjincompany.devblind.user.dto;

import jakarta.validation.constraints.NotBlank;

public record DeviceTokenRequest(
    @NotBlank(message = "디바이스 토큰은 필수입니다")
    String deviceToken
) {}

