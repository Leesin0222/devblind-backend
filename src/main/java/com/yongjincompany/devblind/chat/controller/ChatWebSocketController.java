package com.yongjincompany.devblind.controller;

import com.yongjincompany.devblind.dto.chat.ChatMessageWebSocketRequest;
import com.yongjincompany.devblind.dto.chat.ChatMessageResponse;
import com.yongjincompany.devblind.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;

    /**
     * WebSocket을 통한 메시지 전송
     * 클라이언트에서 /app/chat/{matchingId}로 메시지를 보내면
     * /topic/chat/{matchingId}로 브로드캐스트됩니다.
     */
    @MessageMapping("/chat/{matchingId}")
    @SendTo("/topic/chat/{matchingId}")
    public ChatMessageResponse sendMessage(@Payload ChatMessageWebSocketRequest messageRequest, 
                                         SimpMessageHeaderAccessor headerAccessor,
                                         Long matchingId) {
        
        // WebSocket 세션에서 사용자 ID 추출
        Long userId = extractUserIdFromHeader(headerAccessor);
        
        log.info("WebSocket 메시지 수신: userId={}, matchingId={}, content={}", 
                userId, matchingId, messageRequest.content());

        // ChatMessageRequest로 변환하여 서비스 호출
        com.yongjincompany.devblind.dto.chat.ChatMessageRequest request = 
                new com.yongjincompany.devblind.dto.chat.ChatMessageRequest(
                        matchingId, 
                        messageRequest.content(), 
                        messageRequest.messageType()
                );

        // 메시지 저장 및 브로드캐스트
        return chatService.sendMessage(userId, request);
    }

    /**
     * WebSocket 연결 시 사용자 등록
     */
    @MessageMapping("/chat/{matchingId}/join")
    @SendTo("/topic/chat/{matchingId}")
    public ChatMessageResponse addUser(@Payload ChatMessageWebSocketRequest messageRequest,
                                     SimpMessageHeaderAccessor headerAccessor,
                                     Long matchingId) {
        
        // 사용자를 WebSocket 세션에 추가
        Long userId = extractUserIdFromHeader(headerAccessor);
        headerAccessor.getSessionAttributes().put("userId", userId);
        headerAccessor.getSessionAttributes().put("matchingId", matchingId);
        
        log.info("WebSocket 연결: userId={}, matchingId={}", userId, matchingId);
        
        // 시스템 메시지로 연결 알림 (실제로는 시스템 메시지 생성)
        return null;
    }

    /**
     * 헤더에서 사용자 ID 추출
     */
    private Long extractUserIdFromHeader(SimpMessageHeaderAccessor headerAccessor) {
        Authentication authentication = headerAccessor.getUser();
        if (authentication != null && authentication.getName() != null) {
            return Long.valueOf(authentication.getName());
        }
        
        // 세션에서 사용자 ID 가져오기 (fallback)
        Object userIdObj = headerAccessor.getSessionAttributes().get("userId");
        if (userIdObj != null) {
            return Long.valueOf(userIdObj.toString());
        }
        
        throw new RuntimeException("사용자 인증 정보를 찾을 수 없습니다.");
    }
}
