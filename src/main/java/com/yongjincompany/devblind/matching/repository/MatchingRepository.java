package com.yongjincompany.devblind.repository;

import com.yongjincompany.devblind.entity.Matching;
import com.yongjincompany.devblind.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MatchingRepository extends JpaRepository<Matching, Long> {

    @Query("SELECT m FROM Matching m WHERE (m.user1 = :user OR m.user2 = :user) AND m.status IN ('MATCHED', 'CHATTING')")
    List<Matching> findActiveMatchingsByUser(@Param("user") User user);

    @Query("SELECT m FROM Matching m WHERE (m.user1 = :user1 AND m.user2 = :user2) OR (m.user1 = :user2 AND m.user2 = :user1)")
    Optional<Matching> findByUsers(@Param("user1") User user1, @Param("user2") User user2);

    @Query("SELECT m FROM Matching m WHERE (m.user1 = :user OR m.user2 = :user) AND m.status = 'MATCHED'")
    List<Matching> findMatchedButNotChattingByUser(@Param("user") User user);

    @Query("SELECT m FROM Matching m WHERE (m.user1 = :user OR m.user2 = :user) AND m.status = 'CHATTING'")
    List<Matching> findChattingMatchingsByUser(@Param("user") User user);
}
