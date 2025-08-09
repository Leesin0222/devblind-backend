package com.yongjincompany.devblind.matching.repository;

import com.yongjincompany.devblind.matching.entity.DailyRecommendation;
import com.yongjincompany.devblind.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DailyRecommendationRepository extends JpaRepository<DailyRecommendation, Long> {

    @Query("SELECT dr FROM DailyRecommendation dr WHERE dr.user = :user AND dr.recommendationDate = :date ORDER BY dr.recommendationOrder")
    List<DailyRecommendation> findByUserAndDate(@Param("user") User user, @Param("date") LocalDate date);

    @Query("SELECT COUNT(dr) FROM DailyRecommendation dr WHERE dr.user = :user AND dr.recommendationDate = :date")
    long countByUserAndDate(@Param("user") User user, @Param("date") LocalDate date);

    @Query("SELECT dr.recommendedUser.id FROM DailyRecommendation dr WHERE dr.user = :user AND dr.recommendationDate = :date")
    List<Long> findRecommendedUserIdsByUserAndDate(@Param("user") User user, @Param("date") LocalDate date);
}
