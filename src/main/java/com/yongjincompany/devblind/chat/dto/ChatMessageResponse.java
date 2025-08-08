package com.yongjincompany.devblind.dto.chat;

import com.yongjincompany.devblind.entity.ChatMessage;

import java.time.LocalDateTime;

public record ChatMessageResponse(
        Long id,
        Long matchingId,
        Long senderId,
        String content,
        ChatMessage.MessageType messageType,
        LocalDateTime createdAt
) {
    public static ChatMessageResponse from(ChatMessage message) {
        return new ChatMessageResponse(
                message.getId(),
                message.getMatchingId(),
                message.getSenderId(),
                message.getContent(),
                message.getMessageType(),
                message.getCreatedAt()
        );
    }
}
