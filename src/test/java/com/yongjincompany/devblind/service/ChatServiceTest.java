package com.yongjincompany.devblind.chat.service;

import com.yongjincompany.devblind.chat.dto.ChatMessageRequest;
import com.yongjincompany.devblind.chat.dto.ChatMessageResponse;
import com.yongjincompany.devblind.chat.dto.ChatRoomResponse;
import com.yongjincompany.devblind.chat.entity.ChatMessage;
import com.yongjincompany.devblind.chat.entity.ChatRoom;
import com.yongjincompany.devblind.matching.entity.Matching;
import com.yongjincompany.devblind.user.entity.User;
import com.yongjincompany.devblind.common.exception.ApiException;
import com.yongjincompany.devblind.chat.repository.ChatMessageRepository;
import com.yongjincompany.devblind.chat.repository.ChatRoomRepository;
import com.yongjincompany.devblind.matching.repository.MatchingRepository;
import com.yongjincompany.devblind.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatMessageRepository chatMessageRepository;
    @Mock
    private ChatRoomRepository chatRoomRepository;
    @Mock
    private MatchingRepository matchingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private com.yongjincompany.devblind.chat.service.ChatService chatService;

    private User user1;
    private User user2;
    private Matching matching;
    private ChatRoom chatRoom;
    private ChatMessage chatMessage;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .id(1L)
                .phoneNumber("01012345678")
                .nickname("사용자1")
                .birth(LocalDate.of(1995, 1, 1))
                .gender(User.Gender.MALE)
                .build();

        user2 = User.builder()
                .id(2L)
                .phoneNumber("01087654321")
                .nickname("사용자2")
                .birth(LocalDate.of(1997, 5, 10))
                .gender(User.Gender.FEMALE)
                .build();

        matching = Matching.builder()
                .id(1L)
                .user1(user1)
                .user2(user2)
                .status(Matching.Status.MATCHED)
                .createdAt(LocalDateTime.now())
                .build();

        chatRoom = ChatRoom.builder()
                .id(1L)
                .matchingId(1L)
                .user1(user1)
                .user2(user2)
                .createdAt(LocalDateTime.now())
                .lastMessageAt(LocalDateTime.now())
                .unreadCountUser1(0)
                .unreadCountUser2(0)
                .build();

        chatMessage = ChatMessage.builder()
                .id(1L)
                .matchingId(1L)
                .senderId(1L)
                .content("안녕하세요!")
                .messageType(ChatMessage.MessageType.TEXT)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("채팅방 목록 조회 성공")
    void getMyChatRooms_Success() {
        // given
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(user1));
        when(chatRoomRepository.findByUserOrderByLastMessageAtDesc(user1))
                .thenReturn(List.of(chatRoom));

        // when
        List<ChatRoomResponse> result = chatService.getMyChatRooms(1L);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).matchingId()).isEqualTo(1L);
        assertThat(result.get(0).otherUserId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("채팅 메시지 목록 조회 성공")
    void getChatMessages_Success() {
        // given
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(user1));
        when(matchingRepository.findById(1L)).thenReturn(Optional.of(matching));
        when(chatRoomRepository.findByMatchingId(1L)).thenReturn(Optional.of(chatRoom));
        when(chatMessageRepository.findByMatchingIdOrderByCreatedAtDesc(1L, any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(chatMessage)));

        // when
        Page<ChatMessageResponse> result = chatService.getChatMessages(1L, 1L, 0, 10);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).content()).isEqualTo("안녕하세요!");
        verify(chatRoomRepository).save(chatRoom); // 읽지 않은 메시지 수 초기화
    }

    @Test
    @DisplayName("메시지 전송 성공")
    void sendMessage_Success() {
        // given
        ChatMessageRequest request = new ChatMessageRequest(1L, "안녕하세요!", "TEXT");
        
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(user1));
        when(matchingRepository.findById(1L)).thenReturn(Optional.of(matching));
        when(chatRoomRepository.findByMatchingId(1L)).thenReturn(Optional.of(chatRoom));
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(chatMessage);

        // when
        ChatMessageResponse result = chatService.sendMessage(1L, request);

        // then
        assertThat(result.content()).isEqualTo("안녕하세요!");
        verify(chatMessageRepository).save(any(ChatMessage.class));
        verify(chatRoomRepository, times(2)).save(chatRoom); // 채팅방 정보 업데이트
        verify(messagingTemplate).convertAndSend("/topic/chat/1", result);
    }

    @Test
    @DisplayName("매칭에 속하지 않은 사용자가 메시지 전송 시 실패")
    void sendMessage_NotInMatching_ThrowsException() {
        // given
        ChatMessageRequest request = new ChatMessageRequest(1L, "안녕하세요!", "TEXT");
        
        when(userRepository.findByIdAndDeletedFalse(999L)).thenReturn(Optional.of(user1));
        when(matchingRepository.findById(1L)).thenReturn(Optional.of(matching));

        // when & then
        assertThatThrownBy(() -> chatService.sendMessage(999L, request))
                .isInstanceOf(ApiException.class);
    }

    @Test
    @DisplayName("비활성 매칭에서 메시지 전송 시 실패")
    void sendMessage_InactiveMatching_ThrowsException() {
        // given
        Matching inactiveMatching = Matching.builder()
                .id(1L)
                .user1(user1)
                .user2(user2)
                .status(Matching.Status.ENDED)
                .build();
        
        ChatMessageRequest request = new ChatMessageRequest(1L, "안녕하세요!", "TEXT");
        
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(user1));
        when(matchingRepository.findById(1L)).thenReturn(Optional.of(inactiveMatching));

        // when & then
        assertThatThrownBy(() -> chatService.sendMessage(1L, request))
                .isInstanceOf(ApiException.class);
    }

    @Test
    @DisplayName("빈 메시지 전송 시 실패")
    void sendMessage_EmptyContent_ThrowsException() {
        // given
        ChatMessageRequest request = new ChatMessageRequest(1L, "", "TEXT");
        
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(user1));
        when(matchingRepository.findById(1L)).thenReturn(Optional.of(matching));

        // when & then
        assertThatThrownBy(() -> chatService.sendMessage(1L, request))
                .isInstanceOf(ApiException.class);
    }

    @Test
    @DisplayName("읽지 않은 메시지 수 조회 성공")
    void getUnreadMessageCount_Success() {
        // given
        chatRoom.incrementUnreadCount(1L); // user1이 메시지를 보내면 user2의 읽지 않은 메시지 수 증가
        when(chatRoomRepository.findByMatchingId(1L)).thenReturn(Optional.of(chatRoom));

        // when
        Integer result = chatService.getUnreadMessageCount(2L, 1L);

        // then
        assertThat(result).isEqualTo(1);
    }

    @Test
    @DisplayName("채팅방이 없을 때 읽지 않은 메시지 수는 0")
    void getUnreadMessageCount_NoChatRoom_ReturnsZero() {
        // given
        when(chatRoomRepository.findByMatchingId(1L)).thenReturn(Optional.empty());

        // when
        Integer result = chatService.getUnreadMessageCount(1L, 1L);

        // then
        assertThat(result).isEqualTo(0);
    }
}
