package com.yongjincompany.devblind.matching.repository;

import com.yongjincompany.devblind.matching.entity.MatchingProfile;
import com.yongjincompany.devblind.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MatchingProfileRepository extends JpaRepository<MatchingProfile, Long> {

    Optional<MatchingProfile> findByUser(User user);

    Optional<MatchingProfile> findByUserId(Long userId);

    @Query("SELECT mp FROM MatchingProfile mp " +
           "WHERE mp.isActive = true " +
           "AND mp.user.gender != :userGender " +
           "AND mp.user.id != :userId " +
           "AND mp.lastActiveAt >= :minActiveTime " +
           "ORDER BY mp.lastActiveAt DESC")
    Page<MatchingProfile> findActiveProfilesForMatching(
            @Param("userGender") User.Gender userGender,
            @Param("userId") Long userId,
            @Param("minActiveTime") LocalDateTime minActiveTime,
            Pageable pageable
    );

    @Query("SELECT mp FROM MatchingProfile mp " +
           "WHERE mp.isActive = true " +
           "AND mp.user.gender != :userGender " +
           "AND mp.user.id != :userId " +
           "AND mp.lastActiveAt >= :minActiveTime " +
           "AND mp.location = :location " +
           "ORDER BY mp.lastActiveAt DESC")
    Page<MatchingProfile> findActiveProfilesByLocation(
            @Param("userGender") User.Gender userGender,
            @Param("userId") Long userId,
            @Param("minActiveTime") LocalDateTime minActiveTime,
            @Param("location") String location,
            Pageable pageable
    );

    List<MatchingProfile> findByIsActiveTrue();
}
