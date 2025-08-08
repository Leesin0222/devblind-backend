package com.yongjincompany.devblind.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "matchings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Matching {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id", nullable = false)
    private User user1; // 매칭된 사용자 1

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id", nullable = false)
    private User user2; // 매칭된 사용자 2

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.MATCHED; // 매칭 상태

    @Column(nullable = false)
    private LocalDateTime matchedAt; // 매칭 성공 시간

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime chatStartedAt; // 채팅 시작 시간
    private Long chatStartedBy; // 채팅을 시작한 사용자 ID

    public enum Status {
        MATCHED, // 매칭됨 (채팅 시작 전)
        CHATTING, // 채팅 중
        ENDED, // 종료
        BLOCKED // 차단
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.matchedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void startChat(Long startedByUserId) {
        this.status = Status.CHATTING;
        this.chatStartedAt = LocalDateTime.now();
        this.chatStartedBy = startedByUserId;
    }

    public void end() {
        this.status = Status.ENDED;
    }

    public void block() {
        this.status = Status.BLOCKED;
    }

    public boolean isMatched() {
        return this.status == Status.MATCHED;
    }

    public boolean isChatting() {
        return this.status == Status.CHATTING;
    }

    public boolean isActive() {
        return this.status == Status.MATCHED || this.status == Status.CHATTING;
    }
}
