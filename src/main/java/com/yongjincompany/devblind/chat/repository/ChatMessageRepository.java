package com.yongjincompany.devblind.chat.repository;

import com.yongjincompany.devblind.chat.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.matchingId = :matchingId ORDER BY cm.createdAt DESC")
    Page<ChatMessage> findByMatchingIdOrderByCreatedAtDesc(@Param("matchingId") Long matchingId, Pageable pageable);
    
    List<ChatMessage> findByMatchingIdOrderByCreatedAtAsc(Long matchingId);
    
    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.matchingId = :matchingId AND cm.senderId != :userId AND cm.isRead = false")
    long countUnreadMessages(@Param("matchingId") Long matchingId, @Param("userId") Long userId);
}
