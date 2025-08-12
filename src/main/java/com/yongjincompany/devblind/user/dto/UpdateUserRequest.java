package com.yongjincompany.devblind.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UpdateUserRequest(
    @NotBlank(message = "닉네임은 필수입니다")
    String nickname,
    
    String bio,
    
    @NotBlank(message = "성별은 필수입니다")
    String gender,
    
    @NotNull(message = "나이는 필수입니다")
    Integer age,
    
    @NotBlank(message = "지역은 필수입니다")
    String location,
    
    String profileImageUrl,
    List<String> techStacks,
    String birth,
    List<Long> techStackIds
) {}
