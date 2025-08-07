package com.yongjincompany.devblind.dto.user;

import com.yongjincompany.devblind.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

public record UpdateUserRequest(
        @NotBlank
        String nickname,
        @NotNull
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate birth,
        @NotNull
        User.Gender gender,
        @NotBlank
        String profileImageUrl,
        @NotEmpty
        List<Long> techStackIds
) {
}
