package com.yongjincompany.devblind.dto.chat;

import com.yongjincompany.devblind.entity.ChatRoom;
import com.yongjincompany.devblind.entity.User;

import java.time.LocalDateTime;

public record ChatRoomResponse(
        Long id,
        Long matchingId,
        Long otherUserId,
        String otherUserNickname,
        String otherUserProfileImageUrl,
        String lastMessage,
        LocalDateTime lastMessageAt,
        Integer unreadCount,
        LocalDateTime createdAt
) {
    public static ChatRoomResponse from(ChatRoom chatRoom, Long currentUserId) {
        User otherUser = chatRoom.getUser1().getId().equals(currentUserId) 
                ? chatRoom.getUser2() 
                : chatRoom.getUser1();
        
        return new ChatRoomResponse(
                chatRoom.getId(),
                chatRoom.getMatchingId(),
                otherUser.getId(),
                otherUser.getNickname(),
                otherUser.getProfileImageUrl(),
                chatRoom.getLastMessage(),
                chatRoom.getLastMessageAt(),
                chatRoom.getUnreadCount(currentUserId),
                chatRoom.getCreatedAt()
        );
    }
}
