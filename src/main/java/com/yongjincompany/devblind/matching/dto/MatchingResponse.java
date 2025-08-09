package com.yongjincompany.devblind.matching.dto;

import java.time.LocalDateTime;

public record MatchingResponse(
    Long matchingId,
    Long otherUserId,
    String otherUserNickname,
    String otherUserProfileImageUrl,
    LocalDateTime matchedAt,
    String status
) {
    public static MatchingResponse from(com.yongjincompany.devblind.matching.entity.Matching matching, Long currentUserId) {
        com.yongjincompany.devblind.user.entity.User otherUser = matching.getUser1().getId().equals(currentUserId) 
                ? matching.getUser2() 
                : matching.getUser1();
        
        return new MatchingResponse(
            matching.getId(),
            otherUser.getId(),
            otherUser.getNickname(),
            otherUser.getProfileImageUrl(),
            matching.getCreatedAt(),
            matching.getStatus().name()
        );
    }
}
