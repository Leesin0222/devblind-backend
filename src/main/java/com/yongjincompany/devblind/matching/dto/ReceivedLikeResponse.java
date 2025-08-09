package com.yongjincompany.devblind.matching.dto;

import java.time.LocalDateTime;

public record ReceivedLikeResponse(
    Long fromUserId,
    String fromUserNickname,
    String fromUserProfileImageUrl,
    String likeType,
    LocalDateTime createdAt
) {
    public static ReceivedLikeResponse from(com.yongjincompany.devblind.matching.entity.UserLike userLike) {
        return new ReceivedLikeResponse(
            userLike.getFromUser().getId(),
            userLike.getFromUser().getNickname(),
            userLike.getFromUser().getProfileImageUrl(),
            userLike.getLikeType().name(),
            userLike.getCreatedAt()
        );
    }
}
