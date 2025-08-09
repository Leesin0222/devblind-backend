package com.yongjincompany.devblind.matching.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public record LikeRequest(
    @NotNull
    Long targetUserId,
    
    @NotNull
    LikeType likeType // LIKE, DISLIKE
) {
    public enum LikeType {
        LIKE, DISLIKE, PULL_REQUEST
    }
}
