package com.yongjincompany.devblind.chat.dto;

import java.time.LocalDateTime;

public record ChatMessageResponse(
    Long id,
    Long senderId,
    String senderNickname,
    String content,
    String messageType,
    LocalDateTime createdAt,
    boolean isRead
) {
    public static ChatMessageResponse from(com.yongjincompany.devblind.chat.entity.ChatMessage message) {
        return new ChatMessageResponse(
            message.getId(),
            message.getSenderId(),
            null, // senderNickname은 별도 조회 필요
            message.getContent(),
            message.getMessageType().name(),
            message.getCreatedAt(),
            false // isRead는 별도 조회 필요
        );
    }
}
