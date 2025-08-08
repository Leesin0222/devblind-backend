package com.yongjincompany.devblind.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record SignupRequest(
        @NotBlank
        String signupToken,
        @NotBlank
        String nickname,
        @NotBlank
        String birth, // "YYYY-MM-DD"
        @NotBlank
        String gender, // "MALE" or "FEMALE"
        @NotBlank
        String profileImageUrl,
        @NotEmpty
        List<Long> techStackIds
) {}
