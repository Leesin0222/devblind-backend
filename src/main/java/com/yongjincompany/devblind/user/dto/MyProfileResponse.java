package com.yongjincompany.devblind.dto.user;

import com.yongjincompany.devblind.entity.TechStack;
import com.yongjincompany.devblind.entity.User;
import lombok.Builder;

import java.util.List;

@Builder
public record MyProfileResponse(
        String phoneNumber,
        String nickname,
        String profileImageUrl,
        String birth,
        String gender,
        List<String> techStacks
) {
    public static MyProfileResponse from(User user, List<TechStack> stacks) {
        return new MyProfileResponse(
                user.getPhoneNumber(),
                user.getNickname(),
                user.getBirth().toString(),
                user.getGender().name(),
                user.getProfileImageUrl(),
                stacks.stream().map(TechStack::getName).toList()
        );
    }
}
