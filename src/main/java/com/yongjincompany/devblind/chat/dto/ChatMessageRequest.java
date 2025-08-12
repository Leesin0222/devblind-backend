package com.yongjincompany.devblind.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChatMessageRequest(
    @NotNull(message = "매칭 ID는 필수입니다")
    Long matchingId,
    
    @NotBlank(message = "메시지 내용은 필수입니다")
    String content,
    
    String messageType
) {
    public ChatMessageRequest {
        if (messageType == null) {
            messageType = "TEXT";
        }
    }
}
