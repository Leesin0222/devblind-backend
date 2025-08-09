package com.yongjincompany.devblind.matching.dto;

public record MatchingScoreResponse(
    Long targetUserId,
    String targetUserNickname,
    Double totalScore,
    Double techScore,
    Double locationScore,
    Double ageScore,
    Double preferenceScore,
    String explanation
) {}
