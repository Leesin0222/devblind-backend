package com.yongjincompany.devblind.dto.chat;

import com.yongjincompany.devblind.entity.ChatMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChatMessageRequest(
        @NotNull
        Long matchingId,
        
        @NotBlank
        String content,
        
        ChatMessage.MessageType messageType
) {
    public ChatMessageRequest {
        if (messageType == null) {
            messageType = ChatMessage.MessageType.TEXT;
        }
    }
}
