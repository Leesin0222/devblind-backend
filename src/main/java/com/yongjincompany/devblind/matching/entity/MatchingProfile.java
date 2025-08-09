package com.yongjincompany.devblind.matching.entity;

import jakarta.persistence.*;
import lombok.*;
import com.yongjincompany.devblind.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "matching_profiles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MatchingProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, length = 500)
    private String introduction; // 자기소개

    @Column(nullable = false, length = 200)
    private String idealType; // 이상형

    @Column(nullable = false, length = 200)
    private String hobby; // 취미

    @Column(nullable = false, length = 100)
    private String job; // 직업

    @Column(nullable = false)
    private Integer age; // 나이

    @Column(nullable = false, length = 100)
    private String location; // 지역

    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true; // 매칭 활성화 여부

        @Column(nullable = false)
    private LocalDateTime lastActiveAt; // 마지막 활동 시간

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.lastActiveAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void updateProfile(String introduction, String idealType, String hobby, 
                            String job, Integer age, String location) {
        this.introduction = introduction;
        this.idealType = idealType;
        this.hobby = hobby;
        this.job = job;
        this.age = age;
        this.location = location;
    }

    public void setActive(Boolean isActive) {
        this.isActive = isActive;
        this.lastActiveAt = LocalDateTime.now();
    }

    public void updateLastActive() {
        this.lastActiveAt = LocalDateTime.now();
    }
}
