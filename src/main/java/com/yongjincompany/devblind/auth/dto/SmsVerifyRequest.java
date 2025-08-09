package com.yongjincompany.devblind.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public record SmsVerifyRequest(
    @NotBlank(message = "전화번호는 필수입니다")
    @Pattern(regexp = "^01[0-9]-?[0-9]{4}-?[0-9]{4}$", message = "올바른 전화번호 형식이 아닙니다")
    String phoneNumber,
    
    @NotBlank(message = "인증번호는 필수입니다")
    @Pattern(regexp = "^[0-9]{6}$", message = "6자리 숫자를 입력해주세요")
    String code
) {}
