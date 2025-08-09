package com.yongjincompany.devblind.chat.service;

import com.yongjincompany.devblind.chat.dto.ChatMessageRequest;
import com.yongjincompany.devblind.chat.dto.ChatMessageResponse;
import com.yongjincompany.devblind.chat.dto.ChatRoomResponse;
import com.yongjincompany.devblind.chat.entity.ChatMessage;
import com.yongjincompany.devblind.chat.entity.ChatRoom;
import com.yongjincompany.devblind.matching.entity.Matching;
import com.yongjincompany.devblind.user.entity.User;
import com.yongjincompany.devblind.common.exception.ApiException;
import com.yongjincompany.devblind.common.exception.ErrorCode;
import com.yongjincompany.devblind.chat.repository.ChatMessageRepository;
import com.yongjincompany.devblind.chat.repository.ChatRoomRepository;
import com.yongjincompany.devblind.matching.repository.MatchingRepository;
import com.yongjincompany.devblind.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MatchingRepository matchingRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 채팅방 목록 조회
     */
    @Transactional(readOnly = true)
    public List<ChatRoomResponse> getMyChatRooms(Long userId) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        List<ChatRoom> chatRooms = chatRoomRepository.findByUserOrderByLastMessageAtDesc(user);
        
        return chatRooms.stream()
                .map(chatRoom -> ChatRoomResponse.from(chatRoom, userId))
                .toList();
    }

    /**
     * 채팅 메시지 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<ChatMessageResponse> getChatMessages(Long userId, Long matchingId, int page, int size) {
        // 매칭에 속한 사용자인지 확인
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        Matching matching = matchingRepository.findById(matchingId)
                .orElseThrow(() -> new ApiException(ErrorCode.MATCHING_NOT_FOUND));

        if (!matching.getUser1().getId().equals(userId) && !matching.getUser2().getId().equals(userId)) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }

        // 채팅방이 없으면 생성
        ChatRoom chatRoom = chatRoomRepository.findByMatchingId(matchingId)
                .orElseGet(() -> createChatRoom(matching));

        // 읽지 않은 메시지 수 초기화
        chatRoom.clearUnreadCount(userId);
        chatRoomRepository.save(chatRoom);

        // 메시지 조회 (최신순)
        Pageable pageable = PageRequest.of(page, size);
        Page<ChatMessage> messages = chatMessageRepository.findByMatchingIdOrderByCreatedAtDesc(matchingId, pageable);
        
        return messages.map(ChatMessageResponse::from);
    }

    /**
     * 메시지 전송 (WebSocket)
     */
    @Transactional
    public ChatMessageResponse sendMessage(Long userId, ChatMessageRequest request) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        // 매칭에 속한 사용자인지 확인
        Matching matching = matchingRepository.findById(request.matchingId())
                .orElseThrow(() -> new ApiException(ErrorCode.MATCHING_NOT_FOUND));

        if (!matching.getUser1().getId().equals(userId) && !matching.getUser2().getId().equals(userId)) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }

        // 매칭이 활성 상태인지 확인
        if (!matching.isActive()) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }

        // 메시지 내용 검증
        if (request.content() == null || request.content().trim().isEmpty()) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR);
        }

        if (request.content().length() > 1000) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR);
        }

        // 채팅방이 없으면 생성
        ChatRoom chatRoom = chatRoomRepository.findByMatchingId(request.matchingId())
                .orElseGet(() -> createChatRoom(matching));

        // 메시지 저장
        ChatMessage message = ChatMessage.builder()
                .matchingId(request.matchingId())
                .senderId(userId)
                .content(request.content())
                .messageType(ChatMessage.MessageType.valueOf(request.messageType()))
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(message);

        // 채팅방 정보 업데이트
        chatRoom.updateLastMessage(request.content());
        chatRoom.incrementUnreadCount(userId);
        chatRoomRepository.save(chatRoom);

        // WebSocket으로 메시지 브로드캐스트
        ChatMessageResponse response = ChatMessageResponse.from(savedMessage);
        messagingTemplate.convertAndSend("/topic/chat/" + request.matchingId(), response);

        log.info("채팅 메시지 전송: userId={}, matchingId={}, messageId={}", 
                userId, request.matchingId(), savedMessage.getId());

        return response;
    }

    /**
     * 채팅방 생성
     */
    private ChatRoom createChatRoom(Matching matching) {
        ChatRoom chatRoom = ChatRoom.builder()
                .matchingId(matching.getId())
                .user1(matching.getUser1())
                .user2(matching.getUser2())
                .build();

        return chatRoomRepository.save(chatRoom);
    }

    /**
     * 읽지 않은 메시지 수 조회
     */
    @Transactional(readOnly = true)
    public Integer getUnreadMessageCount(Long userId, Long matchingId) {
        ChatRoom chatRoom = chatRoomRepository.findByMatchingId(matchingId)
                .orElse(null);

        if (chatRoom == null) {
            return 0;
        }

        return chatRoom.getUnreadCount(userId);
    }

    /**
     * 채팅방 존재 여부 확인
     */
    @Transactional(readOnly = true)
    public boolean existsChatRoom(Long matchingId) {
        return chatRoomRepository.findByMatchingId(matchingId).isPresent();
    }
}
