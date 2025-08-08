package com.yongjincompany.devblind.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yongjincompany.devblind.dto.chat.ChatMessageRequest;
import com.yongjincompany.devblind.entity.*;
import com.yongjincompany.devblind.repository.*;
import com.yongjincompany.devblind.service.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class ChatControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MatchingRepository matchingRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private User user1;
    private User user2;
    private Matching matching;
    private String user1Token;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // 테스트 사용자 생성
        user1 = User.builder()
                .phoneNumber("01012345678")
                .nickname("테스트유저1")
                .birth(LocalDate.of(1995, 1, 1))
                .gender(User.Gender.MALE)
                .build();
        user1 = userRepository.save(user1);

        user2 = User.builder()
                .phoneNumber("01087654321")
                .nickname("테스트유저2")
                .birth(LocalDate.of(1997, 5, 10))
                .gender(User.Gender.FEMALE)
                .build();
        user2 = userRepository.save(user2);

        // 매칭 생성
        matching = Matching.builder()
                .user1(user1)
                .user2(user2)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();
        matching = matchingRepository.save(matching);

        // JWT 토큰 생성
        user1Token = jwtProvider.generateToken(user1.getId());
    }

    @Test
    @DisplayName("채팅방 목록 조회 성공")
    void getMyChatRooms_Success() throws Exception {
        // given
        ChatRoom chatRoom = ChatRoom.builder()
                .matchingId(matching.getId())
                .user1(user1)
                .user2(user2)
                .createdAt(LocalDateTime.now())
                .lastMessageAt(LocalDateTime.now())
                .build();
        chatRoomRepository.save(chatRoom);

        // when & then
        mockMvc.perform(get("/chat/rooms")
                        .header("Authorization", "Bearer " + user1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].matchingId").value(matching.getId()))
                .andExpect(jsonPath("$[0].otherUserId").value(user2.getId()));
    }

    @Test
    @DisplayName("채팅 메시지 목록 조회 성공")
    void getChatMessages_Success() throws Exception {
        // given
        ChatMessage message = ChatMessage.builder()
                .matchingId(matching.getId())
                .senderId(user1.getId())
                .content("안녕하세요!")
                .messageType(ChatMessage.MessageType.TEXT)
                .createdAt(LocalDateTime.now())
                .build();
        chatMessageRepository.save(message);

        // when & then
        mockMvc.perform(get("/chat/rooms/{matchingId}/messages", matching.getId())
                        .header("Authorization", "Bearer " + user1Token)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].content").value("안녕하세요!"))
                .andExpect(jsonPath("$.content[0].senderId").value(user1.getId()));
    }

    @Test
    @DisplayName("메시지 전송 성공")
    void sendMessage_Success() throws Exception {
        // given
        ChatMessageRequest request = new ChatMessageRequest(
                matching.getId(), 
                "안녕하세요!", 
                ChatMessage.MessageType.TEXT
        );

        // when & then
        mockMvc.perform(post("/chat/rooms/{matchingId}/messages", matching.getId())
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("안녕하세요!"))
                .andExpect(jsonPath("$.senderId").value(user1.getId()));
    }

    @Test
    @DisplayName("읽지 않은 메시지 수 조회 성공")
    void getUnreadMessageCount_Success() throws Exception {
        // given
        ChatRoom chatRoom = ChatRoom.builder()
                .matchingId(matching.getId())
                .user1(user1)
                .user2(user2)
                .unreadCountUser1(5)
                .unreadCountUser2(3)
                .createdAt(LocalDateTime.now())
                .lastMessageAt(LocalDateTime.now())
                .build();
        chatRoomRepository.save(chatRoom);

        // when & then
        mockMvc.perform(get("/chat/rooms/{matchingId}/unread-count", matching.getId())
                        .header("Authorization", "Bearer " + user1Token))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    @Test
    @DisplayName("인증되지 않은 사용자 요청 시 실패")
    void unauthorizedRequest_ThrowsException() throws Exception {
        // when & then
        mockMvc.perform(get("/chat/rooms"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("매칭에 속하지 않은 사용자가 메시지 전송 시 실패")
    void sendMessage_NotInMatching_ThrowsException() throws Exception {
        // given
        User otherUser = User.builder()
                .phoneNumber("01011111111")
                .nickname("다른유저")
                .birth(LocalDate.of(1990, 1, 1))
                .gender(User.Gender.MALE)
                .build();
        otherUser = userRepository.save(otherUser);

        String otherUserToken = jwtProvider.generateToken(otherUser.getId());

        ChatMessageRequest request = new ChatMessageRequest(
                matching.getId(), 
                "안녕하세요!", 
                ChatMessage.MessageType.TEXT
        );

        // when & then
        mockMvc.perform(post("/chat/rooms/{matchingId}/messages", matching.getId())
                        .header("Authorization", "Bearer " + otherUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
