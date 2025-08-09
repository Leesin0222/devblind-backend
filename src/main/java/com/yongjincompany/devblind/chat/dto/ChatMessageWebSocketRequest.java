package com.yongjincompany.devblind.chat.dto;

import com.yongjincompany.devblind.chat.entity.ChatMessage;
import jakarta.validation.constraints.NotBlank;

public record ChatMessageWebSocketRequest(
        @NotBlank
        String content,
        
        ChatMessage.MessageType messageType
) {
    public ChatMessageWebSocketRequest {
        if (messageType == null) {
            messageType = ChatMessage.MessageType.TEXT;
        }
    }
}
