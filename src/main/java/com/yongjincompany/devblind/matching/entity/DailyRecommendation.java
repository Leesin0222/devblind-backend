package com.yongjincompany.devblind.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_recommendations", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "recommended_user_id", "recommendation_date"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DailyRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 추천을 받은 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommended_user_id", nullable = false)
    private User recommendedUser; // 추천된 사용자

    @Column(nullable = false)
    private LocalDate recommendationDate; // 추천 날짜

    @Column(nullable = false)
    private Integer recommendationOrder; // 추천 순서 (1~5)

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
