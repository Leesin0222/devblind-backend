package com.yongjincompany.devblind.matching.service;

import com.yongjincompany.devblind.matching.entity.MatchingProfile;
import com.yongjincompany.devblind.user.entity.TechStack;
import com.yongjincompany.devblind.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.List;
import java.util.HashSet;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchingScoreService {

    // 매칭 점수 가중치
    private static final double TECH_STACK_WEIGHT = 0.4; // 40%
    private static final double LOCATION_WEIGHT = 0.2;   // 20%
    private static final double AGE_WEIGHT = 0.2;        // 20%
    private static final double PREFERENCE_WEIGHT = 0.2; // 20%

    /**
     * 종합 점수 계산
     */
    public ScoreBreakdown calculateScoreBreakdown(User user, MatchingProfile userProfile, 
                                                 List<TechStack> userTechStacks, MatchingProfile targetProfile) {
        User targetUser = targetProfile.getUser();
        
        double techScore = calculateTechStackScore(userTechStacks, targetUser);
        double locationScore = calculateLocationScore(userProfile.getLocation(), targetProfile.getLocation());
        double ageScore = calculateAgeScore(userProfile.getAge(), targetProfile.getAge());
        double preferenceScore = calculatePreferenceScore(userProfile, targetProfile);
        
        double totalScore = (techScore * TECH_STACK_WEIGHT) +
                           (locationScore * LOCATION_WEIGHT) +
                           (ageScore * AGE_WEIGHT) +
                           (preferenceScore * PREFERENCE_WEIGHT);
        
        log.debug("점수 계산 완료: userId={}, targetUserId={}, totalScore={}, techScore={}, locationScore={}, ageScore={}, preferenceScore={}",
                user.getId(), targetUser.getId(), totalScore, techScore, locationScore, ageScore, preferenceScore);
        
        return new ScoreBreakdown(totalScore, techScore, locationScore, ageScore, preferenceScore);
    }

    /**
     * 기술 스택 점수 계산 (Jaccard 유사도 + 공통 기술 보너스)
     */
    private double calculateTechStackScore(List<TechStack> userTechStacks, User targetUser) {
        if (userTechStacks.isEmpty() || targetUser.getUserTechStacks().isEmpty()) {
            return 0.0;
        }

        Set<String> userTechNames = userTechStacks.stream()
                .map(TechStack::getName)
                .collect(Collectors.toSet());

        Set<String> targetTechNames = targetUser.getUserTechStacks().stream()
                .map(ut -> ut.getTechStack().getName())
                .collect(Collectors.toSet());

        // Jaccard 유사도 계산
        Set<String> intersection = new HashSet<>(userTechNames);
        intersection.retainAll(targetTechNames);

        Set<String> union = new HashSet<>(userTechNames);
        union.addAll(targetTechNames);

        double jaccardSimilarity = union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
        
        // 공통 기술 스택 보너스 (최대 0.2점)
        double commonTechBonus = Math.min(intersection.size() * 0.1, 0.2);
        
        return Math.min(jaccardSimilarity + commonTechBonus, 1.0);
    }

    /**
     * 지역 점수 계산
     */
    private double calculateLocationScore(String userLocation, String targetLocation) {
        if (userLocation == null || targetLocation == null) {
            return 0.0;
        }

        if (isSameRegion(userLocation, targetLocation)) {
            return 1.0; // 같은 지역
        } else if (isAdjacentRegion(userLocation, targetLocation)) {
            return 0.7; // 인접 지역
        } else {
            return 0.3; // 다른 지역
        }
    }

    /**
     * 나이 점수 계산
     */
    private double calculateAgeScore(Integer userAge, Integer targetAge) {
        if (userAge == null || targetAge == null) {
            return 0.0;
        }

        int ageDifference = Math.abs(userAge - targetAge);
        
        if (ageDifference <= 2) {
            return 1.0; // 2세 이하 차이
        } else if (ageDifference <= 5) {
            return 0.8; // 3-5세 차이
        } else if (ageDifference <= 10) {
            return 0.6; // 6-10세 차이
        } else {
            return 0.3; // 10세 이상 차이
        }
    }

    /**
     * 선호도 점수 계산
     */
    private double calculatePreferenceScore(MatchingProfile userProfile, MatchingProfile targetProfile) {
        double score = 0.0;
        int factorCount = 0;

        // 이상형 매칭
        if (userProfile.getIdealType() != null && targetProfile.getIdealType() != null) {
            double idealTypeScore = calculateKeywordSimilarity(userProfile.getIdealType(), targetProfile.getIdealType());
            score += idealTypeScore;
            factorCount++;
        }

        // 취미 매칭
        if (userProfile.getHobby() != null && targetProfile.getHobby() != null) {
            double hobbyScore = hasSimilarHobby(userProfile.getHobby(), targetProfile.getHobby()) ? 1.0 : 0.0;
            score += hobbyScore;
            factorCount++;
        }

        // 자기소개 매칭
        if (userProfile.getIntroduction() != null && targetProfile.getIntroduction() != null) {
            double introScore = calculateKeywordSimilarity(userProfile.getIntroduction(), targetProfile.getIntroduction());
            score += introScore;
            factorCount++;
        }

        return factorCount > 0 ? score / factorCount : 0.0;
    }

    /**
     * 같은 광역시/도인지 확인
     */
    private boolean isSameRegion(String location1, String location2) {
        if (location1 == null || location2 == null) {
            return false;
        }

        // 광역시/도 매핑
        Map<String, String> regionMap = Map.ofEntries(
                // 특별시
                Map.entry("서울", "서울특별시"),
                Map.entry("서울특별시", "서울특별시"),

                // 광역시
                Map.entry("부산", "부산광역시"),
                Map.entry("부산광역시", "부산광역시"),
                Map.entry("대구", "대구광역시"),
                Map.entry("대구광역시", "대구광역시"),
                Map.entry("인천", "인천광역시"),
                Map.entry("인천광역시", "인천광역시"),
                Map.entry("광주", "광주광역시"),
                Map.entry("광주광역시", "광주광역시"),
                Map.entry("대전", "대전광역시"),
                Map.entry("대전광역시", "대전광역시"),
                Map.entry("울산", "울산광역시"),
                Map.entry("울산광역시", "울산광역시"),

                // 특별자치시
                Map.entry("세종", "세종특별자치시"),
                Map.entry("세종특별자치시", "세종특별자치시"),

                // 도
                Map.entry("경기", "경기도"),
                Map.entry("경기도", "경기도"),
                Map.entry("강원", "강원도"),
                Map.entry("강원도", "강원도"),
                Map.entry("충북", "충청북도"),
                Map.entry("충청북도", "충청북도"),
                Map.entry("충남", "충청남도"),
                Map.entry("충청남도", "충청남도"),
                Map.entry("전북", "전라북도"),
                Map.entry("전라북도", "전라북도"),
                Map.entry("전남", "전라남도"),
                Map.entry("전라남도", "전라남도"),
                Map.entry("경북", "경상북도"),
                Map.entry("경상북도", "경상북도"),
                Map.entry("경남", "경상남도"),
                Map.entry("경상남도", "경상남도"),
                Map.entry("제주", "제주특별자치도"),
                Map.entry("제주특별자치도", "제주특별자치도")
        );

        String region1 = regionMap.getOrDefault(location1, location1);
        String region2 = regionMap.getOrDefault(location2, location2);

        return region1.equals(region2);
    }

    /**
     * 인접 지역인지 확인
     */
    private boolean isAdjacentRegion(String location1, String location2) {
        if (location1 == null || location2 == null) {
            return false;
        }

        // 인접 지역 매핑
        Map<String, Set<String>> adjacentRegions = Map.ofEntries(
                // 수도권
                Map.entry("서울특별시", Set.of("인천광역시", "경기도")),
                Map.entry("인천광역시", Set.of("서울특별시", "경기도")),
                Map.entry("경기도", Set.of("서울특별시", "인천광역시", "강원도", "충청북도")),

                // 강원도
                Map.entry("강원도", Set.of("경기도", "충청북도", "경상북도")),

                // 충청도
                Map.entry("충청북도", Set.of("경기도", "강원도", "충청남도", "전라북도")),
                Map.entry("충청남도", Set.of("충청북도", "전라북도", "전라남도", "경상북도")),

                // 전라도
                Map.entry("전라북도", Set.of("충청북도", "충청남도", "전라남도", "경상북도")),
                Map.entry("전라남도", Set.of("충청남도", "전라북도", "경상북도", "경상남도")),

                // 경상도
                Map.entry("경상북도", Set.of("강원도", "충청남도", "전라북도", "전라남도", "경상남도")),
                Map.entry("경상남도", Set.of("충청남도", "전라남도", "경상북도", "부산광역시", "울산광역시")),

                // 부산/울산
                Map.entry("부산광역시", Set.of("울산광역시", "경상남도")),
                Map.entry("울산광역시", Set.of("부산광역시", "경상남도")),

                // 대구
                Map.entry("대구광역시", Set.of("경상북도")),

                // 대전
                Map.entry("대전광역시", Set.of("충청남도", "충청북도")),

                // 광주
                Map.entry("광주광역시", Set.of("전라남도", "전라북도")),

                // 세종
                Map.entry("세종특별자치도", Set.of("충청남도", "충청북도")),

                // 제주
                Map.entry("제주특별자치도", Set.of()) // 제주는 인접 지역 없음
        );

        // 지역명 정규화
        String normalizedLocation1 = normalizeLocationName(location1);
        String normalizedLocation2 = normalizeLocationName(location2);

        Set<String> adjacent = adjacentRegions.get(normalizedLocation1);
        return adjacent != null && adjacent.contains(normalizedLocation2);
    }

    /**
     * 지역명 정규화
     */
    private String normalizeLocationName(String location) {
        if (location == null) return null;

        return switch (location) {
            case "서울" -> "서울특별시";
            case "부산" -> "부산광역시";
            case "대구" -> "대구광역시";
            case "인천" -> "인천광역시";
            case "광주" -> "광주광역시";
            case "대전" -> "대전광역시";
            case "울산" -> "울산광역시";
            case "세종" -> "세종특별자치도";
            case "경기" -> "경기도";
            case "강원" -> "강원도";
            case "충북" -> "충청북도";
            case "충남" -> "충청남도";
            case "전북" -> "전라북도";
            case "전남" -> "전라남도";
            case "경북" -> "경상북도";
            case "경남" -> "경상남도";
            case "제주" -> "제주특별자치도";
            default -> location;
        };
    }

    /**
     * 취미 유사성 확인
     */
    private boolean hasSimilarHobby(String hobby1, String hobby2) {
        if (hobby1 == null || hobby2 == null) {
            return false;
        }

        Set<String> keywords1 = extractKeywords(hobby1);
        Set<String> keywords2 = extractKeywords(hobby2);

        if (keywords1.isEmpty() || keywords2.isEmpty()) {
            return false;
        }

        Set<String> intersection = new HashSet<>(keywords1);
        intersection.retainAll(keywords2);

        return !intersection.isEmpty();
    }

    /**
     * 키워드 유사도 계산
     */
    private double calculateKeywordSimilarity(String text1, String text2) {
        if (text1 == null || text2 == null) {
            return 0.0;
        }

        Set<String> keywords1 = extractKeywords(text1);
        Set<String> keywords2 = extractKeywords(text2);

        if (keywords1.isEmpty() || keywords2.isEmpty()) {
            return 0.0;
        }

        Set<String> intersection = new HashSet<>(keywords1);
        intersection.retainAll(keywords2);

        Set<String> union = new HashSet<>(keywords1);
        union.addAll(keywords2);

        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }

    /**
     * 키워드 추출 (개선된 버전)
     */
    private Set<String> extractKeywords(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Set.of();
        }

        // 한국어 불용어 목록 (더 포괄적)
        Set<String> stopWords = Set.of(
                // 조사
                "이", "가", "을", "를", "의", "에", "로", "와", "과", "도", "는", "은", "만", "부터", "까지", "에서", "에게", "께서",
                // 대명사
                "나", "너", "우리", "저희", "그", "그녀", "이것", "저것", "그것", "무엇", "어떤", "어느", "몇",
                // 부사
                "매우", "너무", "정말", "진짜", "아주", "훨씬", "더", "가장", "제일", "바로", "곧", "이미", "아직", "벌써",
                // 접속사
                "그리고", "또는", "하지만", "그런데", "그러나", "그래서", "따라서", "그러므로", "만약", "만일",
                // 일반적인 단어
                "있다", "없다", "하다", "되다", "있다가", "없다가", "하다가", "되다가",
                "같다", "다르다", "크다", "작다", "높다", "낮다", "좋다", "나쁘다"
        );

        // 특수문자 제거 및 정규화
        String normalizedText = text.replaceAll("[^가-힣a-zA-Z0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim()
                .toLowerCase();

        return Arrays.stream(normalizedText.split("\\s+"))
                .filter(word -> word.length() >= 2 && word.length() <= 10) // 2~10자 길이 제한
                .filter(word -> !stopWords.contains(word))
                .filter(word -> !word.matches(".*[0-9].*")) // 숫자가 포함된 단어 제외
                .collect(Collectors.toSet());
    }

    /**
     * 점수 세부 정보를 담는 클래스
     */
    public static class ScoreBreakdown {
        public final double totalScore;
        public final double techScore;
        public final double locationScore;
        public final double ageScore;
        public final double preferenceScore;

        public ScoreBreakdown(double totalScore, double techScore, double locationScore, double ageScore, double preferenceScore) {
            this.totalScore = totalScore;
            this.techScore = techScore;
            this.locationScore = locationScore;
            this.ageScore = ageScore;
            this.preferenceScore = preferenceScore;
        }
    }
}
