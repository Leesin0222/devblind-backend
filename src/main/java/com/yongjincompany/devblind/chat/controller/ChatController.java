package com.yongjincompany.devblind.chat.controller;

import com.yongjincompany.devblind.common.security.AuthUser;
import com.yongjincompany.devblind.chat.dto.ChatMessageRequest;
import com.yongjincompany.devblind.chat.dto.ChatMessageResponse;
import com.yongjincompany.devblind.chat.dto.ChatRoomResponse;
import com.yongjincompany.devblind.chat.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Tag(name = "채팅", description = "채팅 관련 API")
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "내 채팅방 목록 조회", description = "사용자의 모든 채팅방 목록을 조회합니다.")
    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomResponse>> getMyChatRooms(@AuthUser Long userId) {
        List<ChatRoomResponse> chatRooms = chatService.getMyChatRooms(userId);
        return ResponseEntity.ok(chatRooms);
    }

    @Operation(summary = "채팅 메시지 목록 조회", description = "특정 매칭의 채팅 메시지 목록을 조회합니다.")
    @GetMapping("/rooms/{matchingId}/messages")
    public ResponseEntity<Page<ChatMessageResponse>> getChatMessages(
            @AuthUser Long userId,
            @Parameter(description = "매칭 ID") @PathVariable Long matchingId,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "50") int size) {
        
        Page<ChatMessageResponse> messages = chatService.getChatMessages(userId, matchingId, page, size);
        return ResponseEntity.ok(messages);
    }

    @Operation(summary = "메시지 전송", description = "채팅 메시지를 전송합니다.")
    @PostMapping("/rooms/{matchingId}/messages")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @AuthUser Long userId,
            @Parameter(description = "매칭 ID") @PathVariable Long matchingId,
            @Valid @RequestBody ChatMessageRequest request) {
        
        // matchingId를 request에 설정
        ChatMessageRequest messageRequest = new ChatMessageRequest(matchingId, request.content(), request.messageType());
        ChatMessageResponse response = chatService.sendMessage(userId, messageRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "읽지 않은 메시지 수 조회", description = "특정 채팅방의 읽지 않은 메시지 수를 조회합니다.")
    @GetMapping("/rooms/{matchingId}/unread-count")
    public ResponseEntity<Integer> getUnreadMessageCount(
            @AuthUser Long userId,
            @Parameter(description = "매칭 ID") @PathVariable Long matchingId) {
        
        Integer unreadCount = chatService.getUnreadMessageCount(userId, matchingId);
        return ResponseEntity.ok(unreadCount);
    }
}
