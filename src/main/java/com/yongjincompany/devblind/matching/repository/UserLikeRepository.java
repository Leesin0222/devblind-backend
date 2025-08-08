package com.yongjincompany.devblind.repository;

import com.yongjincompany.devblind.entity.User;
import com.yongjincompany.devblind.entity.UserLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserLikeRepository extends JpaRepository<UserLike, Long> {

    Optional<UserLike> findByUserAndTargetUser(User user, User targetUser);

    @Query("SELECT ul FROM UserLike ul WHERE ul.user = :user AND ul.likeType = 'LIKE' " +
           "AND ul.createdAt >= :threeDaysAgo ORDER BY ul.createdAt DESC")
    List<UserLike> findRecentLikesByUser(@Param("user") User user, @Param("threeDaysAgo") LocalDateTime threeDaysAgo);

    @Query("SELECT ul FROM UserLike ul WHERE ul.targetUser = :targetUser AND ul.likeType = 'LIKE' " +
           "AND ul.createdAt >= :threeDaysAgo ORDER BY ul.createdAt DESC")
    List<UserLike> findRecentLikesReceivedByUser(@Param("targetUser") User targetUser, @Param("threeDaysAgo") LocalDateTime threeDaysAgo);

    @Query("SELECT ul FROM UserLike ul WHERE ul.user = :user AND ul.targetUser = :targetUser AND ul.likeType = 'DISLIKE' " +
           "AND ul.createdAt >= :thirtyDaysAgo")
    Optional<UserLike> findRecentDislike(@Param("user") User user, @Param("targetUser") User targetUser, @Param("thirtyDaysAgo") LocalDateTime thirtyDaysAgo);

    boolean existsByUserAndTargetUserAndLikeType(User user, User targetUser, UserLike.LikeType likeType);

    @Query("SELECT COUNT(ul) FROM UserLike ul WHERE ul.user = :user AND ul.likeType = 'LIKE' " +
           "AND ul.targetUser = :targetUser")
    long countLikesBetweenUsers(@Param("user") User user, @Param("targetUser") User targetUser);
}
