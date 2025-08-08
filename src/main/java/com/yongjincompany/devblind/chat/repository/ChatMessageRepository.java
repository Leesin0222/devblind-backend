package com.yongjincompany.devblind.repository;

import com.yongjincompany.devblind.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("SELECT cm FROM ChatMessage cm WHERE cm.matchingId = :matchingId ORDER BY cm.createdAt DESC")
    Page<ChatMessage> findByMatchingIdOrderByCreatedAtDesc(@Param("matchingId") Long matchingId, Pageable pageable);

    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.matchingId = :matchingId AND cm.senderId != :userId AND cm.createdAt > :lastReadAt")
    long countUnreadMessages(@Param("matchingId") Long matchingId, @Param("userId") Long userId, @Param("lastReadAt") LocalDateTime lastReadAt);

    @Query("SELECT cm FROM ChatMessage cm WHERE cm.matchingId = :matchingId AND cm.createdAt > :since ORDER BY cm.createdAt ASC")
    List<ChatMessage> findNewMessages(@Param("matchingId") Long matchingId, @Param("since") LocalDateTime since);

    @Query("SELECT cm FROM ChatMessage cm WHERE cm.matchingId = :matchingId ORDER BY cm.createdAt DESC LIMIT 1")
    ChatMessage findLastMessageByMatchingId(@Param("matchingId") Long matchingId);
}
