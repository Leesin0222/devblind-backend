package com.yongjincompany.devblind.matching.repository;

import com.yongjincompany.devblind.matching.entity.AdditionalRecommendationUsage;
import com.yongjincompany.devblind.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface AdditionalRecommendationUsageRepository extends JpaRepository<AdditionalRecommendationUsage, Long> {
    
    Optional<AdditionalRecommendationUsage> findByUserAndUsageDate(User user, LocalDate usageDate);
}
