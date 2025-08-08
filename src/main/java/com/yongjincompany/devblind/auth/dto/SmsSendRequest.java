package com.yongjincompany.devblind.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SmsSendRequest (
    @NotBlank(message = "전화번호를 입력해주세요")
    @Pattern(regexp = "[0-9]{10,11}", message = "-없이 10~11자리 숫자를 입력해주세요")
    String phoneNumber
) {}
