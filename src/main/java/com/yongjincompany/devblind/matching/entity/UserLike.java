package com.yongjincompany.devblind.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_likes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "target_user_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 좋아요/싫어요를 누른 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id", nullable = false)
    private User targetUser; // 좋아요/싫어요를 받은 사용자

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LikeType likeType; // LIKE, DISLIKE

    @Column(nullable = false)
    @Index(name = "idx_user_likes_created_at")
    private LocalDateTime createdAt;

    public enum LikeType {
        LIKE, DISLIKE, PULL_REQUEST
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public boolean isLike() {
        return this.likeType == LikeType.LIKE;
    }

    public boolean isDislike() {
        return this.likeType == LikeType.DISLIKE;
    }

    public boolean isPullRequest() {
        return this.likeType == LikeType.PULL_REQUEST;
    }
}
