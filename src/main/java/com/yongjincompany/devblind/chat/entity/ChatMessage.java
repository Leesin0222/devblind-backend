package com.yongjincompany.devblind.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long matchingId; // 매칭 ID

    @Column(nullable = false)
    private Long senderId; // 발신자 ID

    @Column(nullable = false, length = 1000)
    private String content; // 메시지 내용

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType messageType = MessageType.TEXT; // 메시지 타입

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum MessageType {
        TEXT, IMAGE, FILE, CODE
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
