package com.yongjincompany.devblind.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record SignupRequest(
    @NotBlank(message = "전화번호는 필수입니다")
    @Pattern(regexp = "^01[0-9]-?[0-9]{4}-?[0-9]{4}$", message = "올바른 전화번호 형식이 아닙니다")
    String phoneNumber,
    
    @NotBlank(message = "닉네임은 필수입니다")
    String nickname,
    
    @NotBlank(message = "성별은 필수입니다")
    @Pattern(regexp = "^(MALE|FEMALE)$", message = "성별은 MALE 또는 FEMALE이어야 합니다")
    String gender,
    
    @NotNull(message = "나이는 필수입니다")
    Integer age,
    
    @NotBlank(message = "지역은 필수입니다")
    String location,

    @NotBlank(message = "자기소개는 필수입니다")
    String bio,

    @NotNull(message = "기술스택은 필수입니다")
    List<Long> techStackIds,
    
    @NotBlank(message = "SMS 인증 토큰은 필수입니다")
    String signupToken,
    
    @NotBlank(message = "생년월일은 필수입니다")
    String birth,
    
    @NotBlank(message = "프로필 이미지는 필수입니다")
    String profileImageUrl
) {}
