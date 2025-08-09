package com.yongjincompany.devblind.matching.dto;

import java.util.List;

public record MatchingProfileResponse(
    Long userId,
    String nickname,
    String bio,
    String gender,
    Integer age,
    String location,
    String profileImageUrl,
    List<String> techStacks,
    Double score,
    Double techScore,
    Double locationScore,
    Double ageScore,
    Double preferenceScore
) {
    public static MatchingProfileResponse from(com.yongjincompany.devblind.matching.entity.MatchingProfile profile, 
                                             List<String> techStacks, 
                                             Double score,
                                             Double techScore,
                                             Double locationScore,
                                             Double ageScore,
                                             Double preferenceScore) {
        return new MatchingProfileResponse(
            profile.getUser().getId(),
            profile.getUser().getNickname(),
            profile.getUser().getBio(),
            profile.getUser().getGender().name(),
            profile.getUser().getAge(),
            profile.getUser().getLocation(),
            profile.getUser().getProfileImageUrl(),
            techStacks,
            score,
            techScore,
            locationScore,
            ageScore,
            preferenceScore
        );
    }
    
    public static MatchingProfileResponse fromWithScores(com.yongjincompany.devblind.matching.entity.MatchingProfile profile,
                                                        List<com.yongjincompany.devblind.user.entity.TechStack> techStacks,
                                                        Double totalScore,
                                                        Double techScore,
                                                        Double locationScore,
                                                        Double ageScore,
                                                        Double preferenceScore) {
        List<String> techStackNames = techStacks.stream()
                .map(com.yongjincompany.devblind.user.entity.TechStack::getName)
                .toList();
        
        return new MatchingProfileResponse(
            profile.getUser().getId(),
            profile.getUser().getNickname(),
            profile.getUser().getBio(),
            profile.getUser().getGender().name(),
            profile.getUser().getAge(),
            profile.getUser().getLocation(),
            profile.getUser().getProfileImageUrl(),
            techStackNames,
            totalScore,
            techScore,
            locationScore,
            ageScore,
            preferenceScore
        );
    }
}
