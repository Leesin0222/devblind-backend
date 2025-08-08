package com.yongjincompany.devblind.user.service;

import com.yongjincompany.devblind.user.dto.MyProfileResponse;
import com.yongjincompany.devblind.user.dto.UpdateUserRequest;
import com.yongjincompany.devblind.user.entity.TechStack;
import com.yongjincompany.devblind.user.entity.User;
import com.yongjincompany.devblind.user.entity.UserTechStack;
import com.yongjincompany.devblind.common.exception.ApiException;
import com.yongjincompany.devblind.common.exception.ErrorCode;
import com.yongjincompany.devblind.user.repository.UserRepository;
import com.yongjincompany.devblind.user.repository.UserTechStackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserTechStackRepository userTechStackRepository;

    public void updateUser(String phoneNumber, UpdateUserRequest request) {
        log.info("사용자 프로필 업데이트 요청: phoneNumber={}", phoneNumber);
        
        User user = userRepository.findByPhoneNumberAndDeletedFalse(phoneNumber)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        user.updateProfile(
                request.nickname(),
                request.birth(),
                request.gender(),
                request.profileImageUrl()
        );

        List<TechStack> stacks = userTechStackRepository.findByIdIn(request.techStackIds());
        user.setTechStacks(stacks);

        userRepository.save(user);
        log.info("사용자 프로필 업데이트 완료: userId={}", user.getId());
    }

    public void deleteUser(String phoneNumber) {
        log.info("사용자 삭제 요청: phoneNumber={}", phoneNumber);
        
        User user = userRepository.findByPhoneNumberAndDeletedFalse(phoneNumber)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        user.delete();
        log.info("사용자 삭제 완료: userId={}", user.getId());
    }

    public MyProfileResponse getMyProfile(String phoneNumber) {
        log.debug("내 프로필 조회 요청: phoneNumber={}", phoneNumber);
        
        User user = userRepository.findByPhoneNumberAndDeletedFalse(phoneNumber)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        List<TechStack> techStacks = userTechStackRepository.findByUser(user)
                .stream().map(UserTechStack::getTechStack).toList();

        MyProfileResponse response = MyProfileResponse.from(user, techStacks);
        log.debug("내 프로필 조회 완료: userId={}", user.getId());
        
        return response;
    }

    public MyProfileResponse getMyProfile(Long userId) {
        log.debug("내 프로필 조회 요청: userId={}", userId);
        
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        List<TechStack> techStacks = userTechStackRepository.findByUser(user)
                .stream().map(UserTechStack::getTechStack).toList();

        MyProfileResponse response = MyProfileResponse.from(user, techStacks);
        log.debug("내 프로필 조회 완료: userId={}", userId);
        
        return response;
    }

    public boolean isNicknameDuplicate(String nickname) {
        log.debug("닉네임 중복 확인: nickname={}", nickname);
        boolean isDuplicate = userRepository.existsByNicknameAndDeletedFalse(nickname);
        log.debug("닉네임 중복 확인 결과: nickname={}, isDuplicate={}", nickname, isDuplicate);
        return isDuplicate;
    }
}
