package com.yongjincompany.devblind.service;

import com.yongjincompany.devblind.matching.dto.LikeRequest;
import com.yongjincompany.devblind.matching.dto.LikeResponse;
import com.yongjincompany.devblind.matching.dto.MatchingProfileRequest;
import com.yongjincompany.devblind.matching.dto.MatchingProfileResponse;
import com.yongjincompany.devblind.matching.entity.MatchingProfile;
import com.yongjincompany.devblind.matching.entity.UserLike;
import com.yongjincompany.devblind.user.entity.TechStack;
import com.yongjincompany.devblind.user.entity.User;
import com.yongjincompany.devblind.common.exception.ApiException;
import com.yongjincompany.devblind.matching.repository.DailyRecommendationRepository;
import com.yongjincompany.devblind.matching.repository.MatchingProfileRepository;
import com.yongjincompany.devblind.matching.repository.MatchingRepository;
import com.yongjincompany.devblind.matching.repository.UserLikeRepository;
import com.yongjincompany.devblind.matching.service.MatchingScoreService;
import com.yongjincompany.devblind.user.repository.UserRepository;
import com.yongjincompany.devblind.user.repository.UserTechStackRepository;
import com.yongjincompany.devblind.user.service.UserBalanceService;
import com.yongjincompany.devblind.auth.service.FcmService;
import com.yongjincompany.devblind.user.repository.DeviceTokenRepository;
import com.yongjincompany.devblind.matching.repository.AdditionalRecommendationUsageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatchingServiceTest {

    @Mock
    private MatchingProfileRepository matchingProfileRepository;
    @Mock
    private UserLikeRepository userLikeRepository;
    @Mock
    private MatchingRepository matchingRepository;
    @Mock
    private DailyRecommendationRepository dailyRecommendationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserTechStackRepository userTechStackRepository;
    @Mock
    private UserBalanceService userBalanceService;
    @Mock
    private FcmService fcmService;
    @Mock
    private MatchingScoreService matchingScoreService;
    @Mock
    private DeviceTokenRepository deviceTokenRepository;
    @Mock
    private AdditionalRecommendationUsageRepository additionalRecommendationUsageRepository;

    @InjectMocks
    private com.yongjincompany.devblind.matching.service.MatchingService matchingService;

    private User testUser;
    private User targetUser;
    private MatchingProfile userProfile;
    private MatchingProfile targetProfile;
    private TechStack javaStack;
    private TechStack springStack;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .nickname("테스트유저")
                .gender(User.Gender.MALE)
                .build();

        targetUser = User.builder()
                .id(2L)
                .nickname("타겟유저")
                .gender(User.Gender.FEMALE)
                .build();

        javaStack = TechStack.builder()
                .id(1L)
                .name("Java")
                .build();

        springStack = TechStack.builder()
                .id(2L)
                .name("Spring Boot")
                .build();

        userProfile = MatchingProfile.builder()
                .id(1L)
                .user(testUser)
                .introduction("안녕하세요")
                .idealType("착한 사람")
                .hobby("코딩")
                .job("개발자")
                .age(25)
                .location("서울")
                .isActive(true)
                .lastActiveAt(LocalDateTime.now())
                .build();

        targetProfile = MatchingProfile.builder()
                .id(2L)
                .user(targetUser)
                .introduction("반갑습니다")
                .idealType("성실한 사람")
                .hobby("독서")
                .job("개발자")
                .age(24)
                .location("서울")
                .isActive(true)
                .lastActiveAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("오늘의 추천 프로필 조회 성공")
    void getTodayRecommendations_Success() {
        // given
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(testUser));
        when(dailyRecommendationRepository.countByUserAndDate(any(), any())).thenReturn(0L);
        when(dailyRecommendationRepository.findRecommendedUserIdsByUserAndDate(any(), any()))
                .thenReturn(List.of());
        when(matchingProfileRepository.findActiveProfilesForMatching(any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(targetProfile)));
        when(matchingProfileRepository.findByUser(testUser)).thenReturn(Optional.of(userProfile));
        when(userTechStackRepository.findByUser(testUser)).thenReturn(List.of());
        when(userTechStackRepository.findByUser(targetUser)).thenReturn(List.of());
        when(userLikeRepository.findRecentDislike(any(), any(), any())).thenReturn(Optional.empty());
        when(matchingScoreService.calculateScoreBreakdown(any(), any(), any(), any()))
                .thenReturn(new MatchingScoreService.ScoreBreakdown(0.8, 0.3, 0.2, 0.15, 0.15));

        // when
        List<MatchingProfileResponse> result = matchingService.getTodayRecommendations(1L);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).userId()).isEqualTo(2L);
        assertThat(result.get(0).nickname()).isEqualTo("타겟유저");
    }

    @Test
    @DisplayName("좋아요 처리 성공")
    void processLike_Success() {
        // given
        LikeRequest request = new LikeRequest(2L, LikeRequest.LikeType.LIKE);
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByIdAndDeletedFalse(2L)).thenReturn(Optional.of(targetUser));
        when(userLikeRepository.findByUserAndTargetUser(any(), any())).thenReturn(Optional.empty());
        when(dailyRecommendationRepository.findRecommendedUserIdsByUserAndDate(any(), any()))
                .thenReturn(List.of(2L));
        when(userLikeRepository.countLikesBetweenUsers(any(), any())).thenReturn(0L);
        when(userLikeRepository.countLikesBetweenUsers(any(), any())).thenReturn(0L);

        // when
        LikeResponse result = matchingService.processLike(1L, request);

        // then
        assertThat(result.isMatch()).isFalse();
        assertThat(result.message()).isEqualTo("좋아요가 처리되었습니다.");
        verify(userLikeRepository).save(any(UserLike.class));
    }

    @Test
    @DisplayName("Pull Request 처리 성공")
    void processPullRequest_Success() {
        // given
        LikeRequest request = new LikeRequest(2L, LikeRequest.LikeType.PULL_REQUEST);
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByIdAndDeletedFalse(2L)).thenReturn(Optional.of(targetUser));
        when(userLikeRepository.findByUserAndTargetUser(any(), any())).thenReturn(Optional.empty());
        when(dailyRecommendationRepository.findRecommendedUserIdsByUserAndDate(any(), any()))
                .thenReturn(List.of(2L));
        when(userLikeRepository.countLikesBetweenUsers(any(), any())).thenReturn(0L);
        when(userLikeRepository.countLikesBetweenUsers(any(), any())).thenReturn(0L);

        // when
        LikeResponse result = matchingService.processLike(1L, request);

        // then
        assertThat(result.isMatch()).isFalse();
        verify(userBalanceService).spend(1L, 50L); // Pull Request 비용 차감
        verify(fcmService).sendPushMessage(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("매칭 프로필 생성 성공")
    void createMatchingProfile_Success() {
        // given
        MatchingProfileRequest request = new MatchingProfileRequest(
                "nickname",        // nickname
                "bio",            // bio
                "MALE",           // gender
                25,               // age
                "서울",            // location
                List.of("Java"),  // techStacks
                "FEMALE",         // preferredGender
                22,               // minAge
                30,               // maxAge
                "서울",            // preferredLocation
                "안녕하세요",       // introduction
                "착한 사람",       // idealType
                "코딩",           // hobby
                "개발자"          // job
        );
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(testUser));
        when(matchingProfileRepository.findByUser(testUser)).thenReturn(Optional.empty());

        // when
        matchingService.createOrUpdateMatchingProfile(1L, request);

        // then
        verify(matchingProfileRepository).save(any(MatchingProfile.class));
    }

    @Test
    @DisplayName("추가 추천 조회 성공")
    void getAdditionalRecommendations_Success() {
        // given
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(testUser));
        when(additionalRecommendationUsageRepository.findByUserAndUsageDate(any(), any()))
                .thenReturn(Optional.empty());
        when(matchingProfileRepository.findActiveProfilesForMatching(any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(targetProfile)));
        when(matchingProfileRepository.findByUser(testUser)).thenReturn(Optional.of(userProfile));
        when(userTechStackRepository.findByUser(testUser)).thenReturn(List.of());
        when(userTechStackRepository.findByUser(targetUser)).thenReturn(List.of());
        when(userLikeRepository.findRecentDislike(any(), any(), any())).thenReturn(Optional.empty());
        when(matchingScoreService.calculateScoreBreakdown(any(), any(), any(), any()))
                .thenReturn(new MatchingScoreService.ScoreBreakdown(0.8, 0.3, 0.2, 0.15, 0.15));

        // when
        List<MatchingProfileResponse> result = matchingService.getAdditionalRecommendations(1L);

        // then
        assertThat(result).isNotEmpty();
        verify(userBalanceService).spend(1L, 30L); // 추가 추천 비용 차감
    }

    @Test
    @DisplayName("사용자 없음 예외")
    void userNotFound_ThrowsException() {
        // given
        when(userRepository.findByIdAndDeletedFalse(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> matchingService.getTodayRecommendations(999L))
                .isInstanceOf(ApiException.class);
    }
}
