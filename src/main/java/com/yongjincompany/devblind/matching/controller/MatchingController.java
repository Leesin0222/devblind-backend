package com.yongjincompany.devblind.matching.controller;

import com.yongjincompany.devblind.common.security.AuthUser;
import com.yongjincompany.devblind.matching.dto.*;
import com.yongjincompany.devblind.matching.service.MatchingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/matching")
@Tag(name = "매칭", description = "사용자 매칭 및 추천 API")
public class MatchingController {

    private final MatchingService matchingService;

    /**
     * 오늘의 추천 프로필 조회 (종합 점수 기반, 하루 5명)
     */
    @GetMapping("/recommendations")
    public ResponseEntity<List<MatchingProfileResponse>> getTodayRecommendations(@AuthUser Long userId) {
        List<MatchingProfileResponse> recommendations = matchingService.getTodayRecommendations(userId);
        return ResponseEntity.ok(recommendations);
    }

    /**
     * 특정 사용자와의 매칭 점수 계산 (테스트용)
     */
    @GetMapping("/score/{targetUserId}")
    public ResponseEntity<MatchingScoreResponse> calculateMatchingScore(
            @AuthUser Long userId,
            @PathVariable Long targetUserId
    ) {
        MatchingScoreResponse scoreResponse = matchingService.calculateMatchingScore(userId, targetUserId);
        return ResponseEntity.ok(scoreResponse);
    }

    /**
     * 좋아요/싫어요 처리
     */
    @PostMapping("/like")
    public ResponseEntity<LikeResponse> processLike(
            @AuthUser Long userId,
            @RequestBody @Valid LikeRequest request
    ) {
        LikeResponse response = matchingService.processLike(userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 받은 좋아요 목록 조회 (3일 이내)
     */
    @GetMapping("/received-likes")
    public ResponseEntity<List<ReceivedLikeResponse>> getReceivedLikes(@AuthUser Long userId) {
        List<ReceivedLikeResponse> receivedLikes = matchingService.getReceivedLikes(userId);
        return ResponseEntity.ok(receivedLikes);
    }

    /**
     * 받은 좋아요에 좋아요로 응답 (매칭 생성)
     */
    @PostMapping("/respond-like/{senderUserId}")
    public ResponseEntity<LikeResponse> respondToReceivedLike(
            @AuthUser Long userId,
            @PathVariable Long senderUserId
    ) {
        LikeResponse response = matchingService.respondToReceivedLike(userId, senderUserId);
        return ResponseEntity.ok(response);
    }

    /**
     * 내 매칭 목록 조회
     */
    @GetMapping("/my-matchings")
    public ResponseEntity<List<MatchingResponse>> getMyMatchings(@AuthUser Long userId) {
        List<MatchingResponse> matchings = matchingService.getMyMatchings(userId);
        return ResponseEntity.ok(matchings);
    }

    /**
     * 채팅 시작 (코인 차감)
     */
    @PostMapping("/start-chat/{matchingId}")
    public ResponseEntity<Void> startChat(
            @AuthUser Long userId,
            @PathVariable Long matchingId
    ) {
        matchingService.startChat(userId, matchingId);
        return ResponseEntity.ok().build();
    }

    /**
     * 매칭 프로필 생성/수정
     */
    @PostMapping("/profile")
    public ResponseEntity<Void> createOrUpdateMatchingProfile(
            @AuthUser Long userId,
            @RequestBody @Valid MatchingProfileRequest request
    ) {
        matchingService.createOrUpdateMatchingProfile(userId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * 추가 추천 프로필 조회 (유료)
     */
    @GetMapping("/additional-recommendations")
    public ResponseEntity<List<MatchingProfileResponse>> getAdditionalRecommendations(@AuthUser Long userId) {
        List<MatchingProfileResponse> recommendations = matchingService.getAdditionalRecommendations(userId);
        return ResponseEntity.ok(recommendations);
    }

    /**
     * 오늘 추가 추천 사용 횟수 조회
     */
    @GetMapping("/additional-recommendations/usage")
    public ResponseEntity<Map<String, Object>> getAdditionalRecommendationUsage(@AuthUser Long userId) {
        Map<String, Object> usage = matchingService.getAdditionalRecommendationUsage(userId);
        return ResponseEntity.ok(usage);
    }

    /**
     * 매칭 활성화/비활성화
     */
    @PutMapping("/active")
    public ResponseEntity<Void> setMatchingActive(
            @AuthUser Long userId,
            @RequestParam boolean isActive
    ) {
        matchingService.setMatchingActive(userId, isActive);
        return ResponseEntity.ok().build();
    }
}
