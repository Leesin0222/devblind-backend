package com.yongjincompany.devblind.matching.entity;

import jakarta.persistence.*;
import lombok.*;
import com.yongjincompany.devblind.user.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "additional_recommendation_usages", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "usage_date"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AdditionalRecommendationUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate usageDate; // 사용 날짜

    @Builder.Default
    @Column(nullable = false)
    private Integer usageCount = 0; // 사용 횟수

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void incrementUsage() {
        this.usageCount++;
    }

    public boolean canUse() {
        return this.usageCount < 2; // 하루 2번까지만 사용 가능
    }
}
