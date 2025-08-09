package com.yongjincompany.devblind.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public record ChatRoomResponse(
    Long matchingId,
    Long otherUserId,
    String otherUserNickname,
    String lastMessage,
    LocalDateTime lastMessageAt,
    int unreadCount,
    String status
) {
    public static ChatRoomResponse from(com.yongjincompany.devblind.chat.entity.ChatRoom chatRoom, Long currentUserId) {
        com.yongjincompany.devblind.user.entity.User otherUser = chatRoom.getUser1().getId().equals(currentUserId) 
                ? chatRoom.getUser2() 
                : chatRoom.getUser1();
        
        return new ChatRoomResponse(
            chatRoom.getMatchingId(),
            otherUser.getId(),
            otherUser.getNickname(),
            chatRoom.getLastMessage(),
            chatRoom.getLastMessageAt(),
            chatRoom.getUnreadCountUser1() + chatRoom.getUnreadCountUser2(),
            "CHATTING"
        );
    }
}
