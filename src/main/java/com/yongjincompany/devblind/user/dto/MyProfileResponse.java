package com.yongjincompany.devblind.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public record MyProfileResponse(
    Long userId,
    String nickname,
    String bio,
    String gender,
    Integer age,
    String location,
    String profileImageUrl,
    List<TechStackResponse> techStacks,
    Long balance
) {
    public static MyProfileResponse from(com.yongjincompany.devblind.user.entity.User user, 
                                       List<com.yongjincompany.devblind.user.entity.TechStack> stacks) {
        List<TechStackResponse> techStackResponses = stacks.stream()
                .map(TechStackResponse::from)
                .toList();
        
        return new MyProfileResponse(
            user.getId(),
            user.getNickname(),
            user.getBio(),
            user.getGender().name(),
            user.getAge(),
            user.getLocation(),
            user.getProfileImageUrl(),
            techStackResponses,
            null // balance는 별도 조회 필요
        );
    }
}
