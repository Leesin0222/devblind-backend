package com.yongjincompany.devblind.repository;

import com.yongjincompany.devblind.entity.AdditionalRecommendationUsage;
import com.yongjincompany.devblind.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface AdditionalRecommendationUsageRepository extends JpaRepository<AdditionalRecommendationUsage, Long> {
    
    Optional<AdditionalRecommendationUsage> findByUserAndUsageDate(User user, LocalDate usageDate);
}
