package com.yongjincompany.devblind.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_rooms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long matchingId; // 매칭 ID (1:1 관계)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id", nullable = false)
    private User user1; // 채팅방 사용자 1

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id", nullable = false)
    private User user2; // 채팅방 사용자 2

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private LocalDateTime lastMessageAt; // 마지막 메시지 시간

    @Column(length = 500)
    private String lastMessage; // 마지막 메시지 내용

    @Column(nullable = false)
    private Integer unreadCountUser1 = 0; // user1의 읽지 않은 메시지 수

    @Column(nullable = false)
    private Integer unreadCountUser2 = 0; // user2의 읽지 않은 메시지 수

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.lastMessageAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void updateLastMessage(String message) {
        this.lastMessage = message;
        this.lastMessageAt = LocalDateTime.now();
    }

    public void incrementUnreadCount(Long userId) {
        if (user1.getId().equals(userId)) {
            this.unreadCountUser2++;
        } else if (user2.getId().equals(userId)) {
            this.unreadCountUser1++;
        }
    }

    public void clearUnreadCount(Long userId) {
        if (user1.getId().equals(userId)) {
            this.unreadCountUser1 = 0;
        } else if (user2.getId().equals(userId)) {
            this.unreadCountUser2 = 0;
        }
    }

    public Integer getUnreadCount(Long userId) {
        if (user1.getId().equals(userId)) {
            return unreadCountUser1;
        } else if (user2.getId().equals(userId)) {
            return unreadCountUser2;
        }
        return 0;
    }
}
