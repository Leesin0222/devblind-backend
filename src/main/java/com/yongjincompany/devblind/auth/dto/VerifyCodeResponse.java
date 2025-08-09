package com.yongjincompany.devblind.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public record VerifyCodeResponse(
    boolean isRegistered,
    AuthResponse authResponse,
    String signupToken
) {}