package com.yongjincompany.devblind.matching.service;

import com.yongjincompany.devblind.matching.dto.LikeRequest;
import com.yongjincompany.devblind.matching.dto.LikeResponse;
import com.yongjincompany.devblind.matching.dto.MatchingProfileRequest;
import com.yongjincompany.devblind.matching.dto.MatchingProfileResponse;
import com.yongjincompany.devblind.matching.dto.MatchingResponse;
import com.yongjincompany.devblind.matching.dto.MatchingScoreResponse;
import com.yongjincompany.devblind.matching.dto.ReceivedLikeResponse;
import com.yongjincompany.devblind.matching.entity.AdditionalRecommendationUsage;
import com.yongjincompany.devblind.matching.entity.DailyRecommendation;
import com.yongjincompany.devblind.matching.entity.Matching;
import com.yongjincompany.devblind.matching.entity.MatchingProfile;
import com.yongjincompany.devblind.matching.entity.UserLike;
import com.yongjincompany.devblind.user.entity.TechStack;
import com.yongjincompany.devblind.user.entity.User;
import com.yongjincompany.devblind.user.entity.UserTechStack;
import com.yongjincompany.devblind.common.exception.ApiException;
import com.yongjincompany.devblind.common.exception.ErrorCode;
import com.yongjincompany.devblind.matching.repository.AdditionalRecommendationUsageRepository;
import com.yongjincompany.devblind.matching.repository.DailyRecommendationRepository;
import com.yongjincompany.devblind.matching.repository.MatchingProfileRepository;
import com.yongjincompany.devblind.matching.repository.MatchingRepository;
import com.yongjincompany.devblind.matching.repository.UserLikeRepository;
import com.yongjincompany.devblind.user.repository.DeviceTokenRepository;
import com.yongjincompany.devblind.user.repository.UserRepository;
import com.yongjincompany.devblind.user.repository.UserTechStackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchingService {

    private final MatchingProfileRepository matchingProfileRepository;
    private final UserLikeRepository userLikeRepository;
    private final MatchingRepository matchingRepository;
    private final DailyRecommendationRepository dailyRecommendationRepository;
    private final UserRepository userRepository;
    private final UserTechStackRepository userTechStackRepository;
    private final com.yongjincompany.devblind.user.service.UserBalanceService userBalanceService;
    private final com.yongjincompany.devblind.auth.service.FcmService fcmService;
    private final com.yongjincompany.devblind.matching.service.MatchingScoreService matchingScoreService;
    private final DeviceTokenRepository deviceTokenRepository;
    private final AdditionalRecommendationUsageRepository additionalRecommendationUsageRepository;

    private static final int DAILY_RECOMMENDATION_LIMIT = 5;
    private static final int DISLIKE_BLOCK_DAYS = 30;
    private static final int LIKE_EXPIRE_DAYS = 3;
    private static final int ADDITIONAL_RECOMMENDATION_LIMIT = 2; // 하루 추가 추천 제한
    private static final int ADDITIONAL_RECOMMENDATION_COUNT = 2; // 추가 추천 인원 수
    private static final long CHAT_START_COST = 100L; // 채팅 시작 비용
    private static final long PULL_REQUEST_COST = 50L; // Pull Request 비용
    private static final long ADDITIONAL_RECOMMENDATION_COST = 30L; // 추가 추천 비용

    /**
     * 오늘의 추천 프로필 조회 (종합 점수 기반, 하루 5명)
     */
    @Transactional(readOnly = true)
    public List<MatchingProfileResponse> getTodayRecommendations(Long userId) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        LocalDate today = LocalDate.now();
        
        // 오늘 이미 추천받은 사용자들 조회
        List<Long> alreadyRecommendedUserIds = dailyRecommendationRepository
                .findRecommendedUserIdsByUserAndDate(user, today);

        // 오늘 추천받은 수 확인
        long todayRecommendationCount = dailyRecommendationRepository.countByUserAndDate(user, today);
        
        if (todayRecommendationCount >= DAILY_RECOMMENDATION_LIMIT) {
            log.info("오늘 추천 한도를 초과했습니다. userId: {}", userId);
            return List.of();
        }

        // 30일 내에 싫어요를 누른 사용자들 제외
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(DISLIKE_BLOCK_DAYS);
        
        // 추천할 프로필 조회 (더 많은 후보를 가져와서 점수 계산 후 상위 5명 선택)
        Page<MatchingProfile> candidateProfiles = matchingProfileRepository
                .findActiveProfilesForMatching(
                        user.getGender(),
                        userId,
                        LocalDateTime.now().minusDays(7), // 7일 내 활동한 사용자만
                        PageRequest.of(0, 50) // 후보 50명까지 가져오기
                );

        // 사용자의 매칭 프로필 조회
        MatchingProfile userProfile = matchingProfileRepository.findByUser(user)
                .orElseThrow(() -> new ApiException(ErrorCode.MATCHING_PROFILE_NOT_FOUND));

        // 사용자의 기술 스택 조회
        List<TechStack> userTechStacks = userTechStackRepository.findByUser(user)
                .stream().map(UserTechStack::getTechStack).toList();

        // 종합 점수 계산 및 정렬
        List<ScoredProfile> scoredProfiles = candidateProfiles.getContent().stream()
                .filter(profile -> !alreadyRecommendedUserIds.contains(profile.getUser().getId()))
                .filter(profile -> !hasRecentDislike(user, profile.getUser(), thirtyDaysAgo))
                .map(profile -> {
                    MatchingScoreService.ScoreBreakdown scoreBreakdown = matchingScoreService.calculateScoreBreakdown(user, userProfile, userTechStacks, profile);
                    return new ScoredProfile(profile, scoreBreakdown);
                })
                .sorted(Comparator.comparing((ScoredProfile sp) -> sp.scoreBreakdown().totalScore).reversed())
                .limit(DAILY_RECOMMENDATION_LIMIT - todayRecommendationCount)
                .toList();

        log.info("사용자 {}의 추천 프로필 점수: {}", userId, 
                scoredProfiles.stream()
                        .map(sp -> String.format("%s(%.2f)", sp.profile().getUser().getNickname(), sp.scoreBreakdown().totalScore))
                        .collect(Collectors.joining(", ")));

        // 응답 생성
        return scoredProfiles.stream()
                .map(scoredProfile -> {
                    List<TechStack> techStacks = userTechStackRepository.findByUser(scoredProfile.profile.getUser())
                            .stream().map(UserTechStack::getTechStack).toList();
                    return MatchingProfileResponse.fromWithScores(
                            scoredProfile.profile(), 
                            techStacks,
                            scoredProfile.scoreBreakdown().totalScore,
                            scoredProfile.scoreBreakdown().techScore,
                            scoredProfile.scoreBreakdown().locationScore,
                            scoredProfile.scoreBreakdown().ageScore,
                            scoredProfile.scoreBreakdown().preferenceScore
                    );
                })
                .toList();
    }

    /**
     * 종합 매칭 점수 계산 (상세 점수 포함)
     */






    /**
     * 좋아요/싫어요 처리
     */
    @Transactional
    public LikeResponse processLike(Long userId, LikeRequest request) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        
        User targetUser = userRepository.findByIdAndDeletedFalse(request.targetUserId())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        if (user.getId().equals(targetUser.getId())) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR);
        }

        // 이미 처리한 경우 확인
        Optional<UserLike> existingLike = userLikeRepository.findByUserAndTargetUser(user, targetUser);
        if (existingLike.isPresent()) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR);
        }

        // 오늘 추천 목록에 있는지 확인
        LocalDate today = LocalDate.now();
        List<Long> todayRecommendations = dailyRecommendationRepository
                .findRecommendedUserIdsByUserAndDate(user, today);
        
        if (!todayRecommendations.contains(targetUser.getId())) {
            throw new ApiException(ErrorCode.DAILY_RECOMMENDATION_LIMIT_EXCEEDED);
        }

        // Pull Request인 경우 코인 차감 및 특별 알림
        if (request.likeType() == LikeRequest.LikeType.PULL_REQUEST) {
            userBalanceService.spend(userId, PULL_REQUEST_COST);
            log.info("Pull Request 사용: userId={}, targetUserId={}, cost={}", userId, request.targetUserId(), PULL_REQUEST_COST);
            
            // Pull Request 특별 알림 발송
            sendPullRequestNotification(request.targetUserId(), user.getNickname());
        }

        // 좋아요/싫어요/PR 저장
        UserLike userLike = UserLike.builder()
                .user(user)
                .targetUser(targetUser)
                .likeType(convertLikeType(request.likeType()))
                .build();
        userLikeRepository.save(userLike);

        // 오늘 추천 목록에 추가
        long todayCount = dailyRecommendationRepository.countByUserAndDate(user, today);
        DailyRecommendation recommendation = DailyRecommendation.builder()
                .user(user)
                .recommendedUser(targetUser)
                .recommendationDate(today)
                .recommendationOrder((int) todayCount + 1)
                .build();
        dailyRecommendationRepository.save(recommendation);

        // 좋아요나 PR인 경우 매칭 확인
        if (request.likeType() == LikeRequest.LikeType.LIKE || request.likeType() == LikeRequest.LikeType.PULL_REQUEST) {
            return checkAndCreateMatching(user, targetUser);
        }

        return new LikeResponse(false, "싫어요가 처리되었습니다.", null);
    }

    /**
     * 받은 좋아요 목록 조회 (3일 이내)
     */
    @Transactional(readOnly = true)
    public List<ReceivedLikeResponse> getReceivedLikes(Long userId) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(LIKE_EXPIRE_DAYS);
        
        List<UserLike> receivedLikes = userLikeRepository.findRecentLikesReceivedByUser(user, threeDaysAgo);
        
        return receivedLikes.stream()
                .map(ReceivedLikeResponse::from)
                .toList();
    }

    /**
     * 받은 좋아요에 좋아요로 응답 (매칭 생성)
     */
    @Transactional
    public LikeResponse respondToReceivedLike(Long userId, Long senderUserId) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        
        User sender = userRepository.findByIdAndDeletedFalse(senderUserId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        // 상대방이 나에게 좋아요를 눌렀는지 확인
        Optional<UserLike> receivedLike = userLikeRepository.findByUserAndTargetUser(sender, user);
        if (receivedLike.isEmpty() || !receivedLike.get().isLike()) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR);
        }

        // 3일 이내인지 확인
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(LIKE_EXPIRE_DAYS);
        if (receivedLike.get().getCreatedAt().isBefore(threeDaysAgo)) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR);
        }

        // 이미 매칭이 있는지 확인
        Optional<Matching> existingMatching = matchingRepository.findByUsers(user, sender);
        if (existingMatching.isPresent()) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR);
        }

        // 나도 상대방에게 좋아요를 눌렀는지 확인
        Optional<UserLike> myLike = userLikeRepository.findByUserAndTargetUser(user, sender);
        if (myLike.isEmpty() || !myLike.get().isLike()) {
            // 좋아요 생성
            UserLike userLike = UserLike.builder()
                    .user(user)
                    .targetUser(sender)
                    .likeType(UserLike.LikeType.LIKE)
                    .build();
            userLikeRepository.save(userLike);
        }

        // 매칭 생성
        return checkAndCreateMatching(user, sender);
    }

    /**
     * 매칭 목록 조회
     */
    @Transactional(readOnly = true)
    public List<MatchingResponse> getMyMatchings(Long userId) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        List<Matching> matchings = matchingRepository.findActiveMatchingsByUser(user);
        
        return matchings.stream()
                .map(matching -> MatchingResponse.from(matching, userId))
                .toList();
    }

    /**
     * 채팅 시작 (코인 차감)
     */
    @Transactional
    public void startChat(Long userId, Long matchingId) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        Matching matching = matchingRepository.findById(matchingId)
                .orElseThrow(() -> new ApiException(ErrorCode.MATCHING_PROFILE_NOT_FOUND));

        // 매칭에 참여한 사용자인지 확인
        if (!matching.getUser1().getId().equals(userId) && !matching.getUser2().getId().equals(userId)) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }

        // 이미 채팅 중인지 확인
        if (matching.isChatting()) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR);
        }

        // 코인 차감
        userBalanceService.spend(userId, CHAT_START_COST);

        // 채팅 시작
        matching.startChat(userId);
        matchingRepository.save(matching);

        // 상대방에게 푸시 알림
        Long otherUserId = matching.getUser1().getId().equals(userId) 
                ? matching.getUser2().getId() 
                : matching.getUser1().getId();
        
        sendChatStartedNotification(otherUserId, user.getNickname());
        log.info("채팅이 시작되었습니다. matchingId: {}, startedBy: {}", matchingId, userId);
    }

    /**
     * 매칭 프로필 생성/수정
     */
    @Transactional
    public void createOrUpdateMatchingProfile(Long userId, MatchingProfileRequest request) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        Optional<MatchingProfile> existingProfile = matchingProfileRepository.findByUser(user);
        
        if (existingProfile.isPresent()) {
            // 프로필 수정
            MatchingProfile profile = existingProfile.get();
            profile.updateProfile(
                    request.introduction(),
                    request.idealType(),
                    request.hobby(),
                    request.job(),
                    request.age(),
                    request.location()
            );
            matchingProfileRepository.save(profile);
        } else {
            // 새 프로필 생성
            MatchingProfile profile = MatchingProfile.builder()
                    .user(user)
                    .introduction(request.introduction())
                    .idealType(request.idealType())
                    .hobby(request.hobby())
                    .job(request.job())
                    .age(request.age())
                    .location(request.location())
                    .build();
            matchingProfileRepository.save(profile);
        }
    }

    /**
     * 특정 사용자와의 매칭 점수 계산 (테스트용)
     */
    @Transactional(readOnly = true)
    public MatchingScoreResponse calculateMatchingScore(Long userId, Long targetUserId) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        
        User targetUser = userRepository.findByIdAndDeletedFalse(targetUserId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        MatchingProfile userProfile = matchingProfileRepository.findByUser(user)
                .orElseThrow(() -> new ApiException(ErrorCode.MATCHING_PROFILE_NOT_FOUND));
        
        MatchingProfile targetProfile = matchingProfileRepository.findByUser(targetUser)
                .orElseThrow(() -> new ApiException(ErrorCode.MATCHING_PROFILE_NOT_FOUND));

        List<TechStack> userTechStacks = userTechStackRepository.findByUser(user)
                .stream().map(UserTechStack::getTechStack).toList();

        MatchingScoreService.ScoreBreakdown scoreBreakdown = matchingScoreService.calculateScoreBreakdown(user, userProfile, userTechStacks, targetProfile);
        
        String explanation = generateScoreExplanation(userProfile, targetProfile, scoreBreakdown);
        
        return new MatchingScoreResponse(
                targetUserId,
                targetUser.getNickname(),
                scoreBreakdown.totalScore,
                scoreBreakdown.techScore,
                scoreBreakdown.locationScore,
                scoreBreakdown.ageScore,
                scoreBreakdown.preferenceScore,
                explanation
        );
    }

    /**
     * 매칭 점수 설명 생성
     */
    private String generateScoreExplanation(MatchingProfile userProfile, MatchingProfile targetProfile, MatchingScoreService.ScoreBreakdown scoreBreakdown) {
        StringBuilder explanation = new StringBuilder();
        
        // 기술 스택 설명
        if (scoreBreakdown.techScore > 0.3) {
            explanation.append("기술 스택이 잘 맞습니다. ");
        } else if (scoreBreakdown.techScore > 0.1) {
            explanation.append("기술 스택이 부분적으로 맞습니다. ");
        } else {
            explanation.append("기술 스택이 다릅니다. ");
        }
        
        // 지역 설명
        if (scoreBreakdown.locationScore > 0.15) {
            explanation.append("같은 지역입니다. ");
        } else if (scoreBreakdown.locationScore > 0.1) {
            explanation.append("인접 지역입니다. ");
        } else {
            explanation.append("멀리 떨어진 지역입니다. ");
        }
        
        // 나이 설명
        if (scoreBreakdown.ageScore > 0.15) {
            explanation.append("나이가 비슷합니다. ");
        } else if (scoreBreakdown.ageScore > 0.1) {
            explanation.append("나이 차이가 적당합니다. ");
        } else {
            explanation.append("나이 차이가 있습니다. ");
        }
        
        // 선호도 설명
        if (scoreBreakdown.preferenceScore > 0.15) {
            explanation.append("취미나 관심사가 잘 맞습니다. ");
        } else if (scoreBreakdown.preferenceScore > 0.1) {
            explanation.append("취미나 관심사가 부분적으로 맞습니다. ");
        } else {
            explanation.append("취미나 관심사가 다릅니다. ");
        }
        
        return explanation.toString();
    }

    /**
     * 추가 추천 프로필 조회 (유료)
     */
    @Transactional
    public List<MatchingProfileResponse> getAdditionalRecommendations(Long userId) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        // 오늘 추가 추천 사용 횟수 확인
        LocalDate today = LocalDate.now();
        AdditionalRecommendationUsage usage = additionalRecommendationUsageRepository
                .findByUserAndUsageDate(user, today)
                .orElseGet(() -> AdditionalRecommendationUsage.builder()
                        .user(user)
                        .usageDate(today)
                        .usageCount(0)
                        .build());

        // 하루 2번 제한 확인
        if (!usage.canUse()) {
            throw new ApiException(ErrorCode.ADDITIONAL_RECOMMENDATION_LIMIT_EXCEEDED);
        }

        // 코인 차감
        userBalanceService.spend(userId, ADDITIONAL_RECOMMENDATION_COST);
        log.info("추가 추천 사용: userId={}, cost={}, usageCount={}", userId, ADDITIONAL_RECOMMENDATION_COST, usage.getUsageCount() + 1);

        // 사용 횟수 증가 및 저장
        usage.incrementUsage();
        additionalRecommendationUsageRepository.save(usage);

        // 추가 추천 로직 (2명 추가 추천)
        return getRecommendationsForUser(user, ADDITIONAL_RECOMMENDATION_COUNT);
    }

    /**
     * 오늘 추가 추천 사용 횟수 조회
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getAdditionalRecommendationUsage(Long userId) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        LocalDate today = LocalDate.now();
        AdditionalRecommendationUsage usage = additionalRecommendationUsageRepository
                .findByUserAndUsageDate(user, today)
                .orElse(AdditionalRecommendationUsage.builder()
                        .user(user)
                        .usageDate(today)
                        .usageCount(0)
                        .build());

        return Map.of(
                "todayUsageCount", usage.getUsageCount(),
                "maxUsageCount", ADDITIONAL_RECOMMENDATION_LIMIT,
                "remainingCount", ADDITIONAL_RECOMMENDATION_LIMIT - usage.getUsageCount(),
                "canUse", usage.canUse()
        );
    }

    /**
     * 사용자에게 추천 프로필 생성 (공통 로직)
     */
    private List<MatchingProfileResponse> getRecommendationsForUser(User user, int count) {
        // 30일 내에 싫어요를 누른 사용자들 제외
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(DISLIKE_BLOCK_DAYS);
        
        // 추천할 프로필 조회 (더 많은 후보를 가져와서 점수 계산 후 상위 선택)
        Page<MatchingProfile> candidateProfiles = matchingProfileRepository
                .findActiveProfilesForMatching(
                        user.getGender(),
                        user.getId(),
                        LocalDateTime.now().minusDays(7), // 7일 내 활동한 사용자만
                        PageRequest.of(0, 50) // 후보 50명까지 가져오기
                );

        // 사용자의 매칭 프로필 조회
        MatchingProfile userProfile = matchingProfileRepository.findByUser(user)
                .orElseThrow(() -> new ApiException(ErrorCode.MATCHING_PROFILE_NOT_FOUND));

        // 사용자의 기술 스택 조회
        List<TechStack> userTechStacks = userTechStackRepository.findByUser(user)
                .stream().map(UserTechStack::getTechStack).toList();

        // 종합 점수 계산 및 정렬
        List<ScoredProfile> scoredProfiles = candidateProfiles.getContent().stream()
                .filter(profile -> !hasRecentDislike(user, profile.getUser(), thirtyDaysAgo))
                .map(profile -> {
                    MatchingScoreService.ScoreBreakdown scoreBreakdown = matchingScoreService.calculateScoreBreakdown(user, userProfile, userTechStacks, profile);
                    return new ScoredProfile(profile, scoreBreakdown);
                })
                .sorted(Comparator.comparing((ScoredProfile sp) -> sp.scoreBreakdown().totalScore).reversed())
                .limit(count)
                .toList();

        log.info("사용자 {}의 추천 프로필 점수: {}", user.getId(), 
                scoredProfiles.stream()
                        .map(sp -> String.format("%s(%.2f)", sp.profile().getUser().getNickname(), sp.scoreBreakdown().totalScore))
                        .collect(Collectors.joining(", ")));

        // 응답 생성
        return scoredProfiles.stream()
                .map(scoredProfile -> {
                    List<TechStack> techStacks = userTechStackRepository.findByUser(scoredProfile.profile.getUser())
                            .stream().map(UserTechStack::getTechStack).toList();
                    return MatchingProfileResponse.fromWithScores(
                            scoredProfile.profile(), 
                            techStacks,
                            scoredProfile.scoreBreakdown().totalScore,
                            scoredProfile.scoreBreakdown().techScore,
                            scoredProfile.scoreBreakdown().locationScore,
                            scoredProfile.scoreBreakdown().ageScore,
                            scoredProfile.scoreBreakdown().preferenceScore
                    );
                })
                .toList();
    }

    /**
     * 매칭 활성화/비활성화
     */
    @Transactional
    public void setMatchingActive(Long userId, boolean isActive) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        MatchingProfile profile = matchingProfileRepository.findByUser(user)
                .orElseThrow(() -> new ApiException(ErrorCode.MATCHING_PROFILE_NOT_FOUND));

        profile.setActive(isActive);
        matchingProfileRepository.save(profile);
    }

    // Private helper methods

    private boolean hasRecentDislike(User user, User targetUser, LocalDateTime since) {
        Optional<UserLike> dislike = userLikeRepository.findRecentDislike(user, targetUser, since);
        return dislike.isPresent();
    }

    private LikeResponse checkAndCreateMatching(User user1, User user2) {
        // 서로 좋아요를 눌렀는지 확인
        long likeCount1 = userLikeRepository.countLikesBetweenUsers(user1, user2);
        long likeCount2 = userLikeRepository.countLikesBetweenUsers(user2, user1);

        if (likeCount1 > 0 && likeCount2 > 0) {
            // 매칭 생성
            Matching matching = Matching.builder()
                    .user1(user1)
                    .user2(user2)
                    .build();
            matchingRepository.save(matching);

            // 푸시 알림 발송
            sendMatchingSuccessNotification(user1, user2);

            log.info("매칭이 성공했습니다! user1: {}, user2: {}", user1.getId(), user2.getId());

            return new LikeResponse(true, "매칭이 성공했습니다!", matching.getId());
        }

        return new LikeResponse(false, "좋아요가 처리되었습니다.", null);
    }

    /**
     * 매칭 성공 알림 발송
     */
    private void sendMatchingSuccessNotification(User user1, User user2) {
        try {
            // user1에게 알림
            fcmService.sendPushMessage(
                    getDeviceToken(user1.getId()),
                    "매칭 성공! 🎉",
                    user2.getNickname() + "님과 매칭되었습니다!"
            );
            
            // user2에게 알림
            fcmService.sendPushMessage(
                    getDeviceToken(user2.getId()),
                    "매칭 성공! 🎉",
                    user1.getNickname() + "님과 매칭되었습니다!"
            );
        } catch (Exception e) {
            log.error("매칭 성공 알림 발송 실패: {}", e.getMessage());
        }
    }

    /**
     * Pull Request 알림 발송
     */
    private void sendPullRequestNotification(Long userId, String senderNickname) {
        try {
            fcmService.sendPushMessage(
                    getDeviceToken(userId),
                    "Pull Request! 🔄",
                    senderNickname + "님이 당신에게 Pull Request를 보냈습니다!"
            );
        } catch (Exception e) {
            log.error("Pull Request 알림 발송 실패: {}", e.getMessage());
        }
    }

    /**
     * 채팅 시작 알림 발송
     */
    private void sendChatStartedNotification(Long userId, String senderNickname) {
        try {
            fcmService.sendPushMessage(
                    getDeviceToken(userId),
                    "채팅 시작! 💬",
                    senderNickname + "님이 채팅을 시작했습니다!"
            );
        } catch (Exception e) {
            log.error("채팅 시작 알림 발송 실패: {}", e.getMessage());
        }
    }

    /**
     * 사용자의 디바이스 토큰 조회
     */
    private String getDeviceToken(Long userId) {
        return deviceTokenRepository.findByUserId(userId)
                .stream().map(deviceToken -> deviceToken.getToken())
                .findFirst()
                .orElse("");
    }

    // 점수 상세 정보를 위한 내부 클래스


    /**
     * LikeRequest.LikeType을 UserLike.LikeType으로 변환
     */
    private UserLike.LikeType convertLikeType(LikeRequest.LikeType requestType) {
        return switch (requestType) {
            case LIKE -> UserLike.LikeType.LIKE;
            case DISLIKE -> UserLike.LikeType.DISLIKE;
            case PULL_REQUEST -> UserLike.LikeType.PULL_REQUEST;
        };
    }

    // 점수가 매겨진 프로필을 위한 내부 record
    private record ScoredProfile(
        MatchingProfile profile, 
        MatchingScoreService.ScoreBreakdown scoreBreakdown
    ) {}
}
