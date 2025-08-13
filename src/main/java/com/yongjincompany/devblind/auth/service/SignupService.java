package com.yongjincompany.devblind.auth.service;

import com.yongjincompany.devblind.common.security.JwtProvider;
import com.yongjincompany.devblind.auth.dto.AuthResponse;
import com.yongjincompany.devblind.auth.dto.SignupRequest;
import com.yongjincompany.devblind.user.entity.TechStack;
import com.yongjincompany.devblind.user.entity.User;
import com.yongjincompany.devblind.common.exception.ApiException;
import com.yongjincompany.devblind.common.exception.ErrorCode;
import com.yongjincompany.devblind.user.repository.TechStackRepository;
import com.yongjincompany.devblind.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SignupService {

    private final RedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository;
    private final TechStackRepository techStackRepository;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    public AuthResponse signup(SignupRequest request) {
        String phoneNumber = redisTemplate.opsForValue().get("signupToken:" + request.signupToken());
        if (phoneNumber == null) {
            throw new ApiException(ErrorCode.INVALID_CODE);
        }

        if (userRepository.findByPhoneNumberAndDeletedFalse(phoneNumber).isPresent()) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR);
        }

        List<TechStack> stacks = request.techStackIds() != null && !request.techStackIds().isEmpty()
                ? techStackRepository.findByIdIn(request.techStackIds())
                : List.of();

        User user = User.builder()
                .phoneNumber(phoneNumber)
                .nickname(request.nickname())
                .birth(LocalDate.parse(request.birth()))
                .gender(User.Gender.valueOf(request.gender().toUpperCase()))
                .profileImageUrl(request.profileImageUrl())
                .age(request.age())
                .bio(request.bio())
                .location(request.location())
                .createdAt(LocalDateTime.now())
                .build();

        user.setTechStacks(stacks);

        userRepository.save(user);
        redisTemplate.delete("signupToken:" + request.signupToken());

        Long userId = user.getId();
        String accessToken = jwtProvider.generateAccessToken(userId);
        String refreshToken = jwtProvider.generateRefreshToken(userId);
        refreshTokenService.saveRefreshToken(userId, refreshToken);

        return new AuthResponse(accessToken, refreshToken, user.getId(), user.getNickname());
    }
}

