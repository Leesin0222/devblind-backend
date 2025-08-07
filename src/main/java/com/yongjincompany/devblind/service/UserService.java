package com.yongjincompany.devblind.service;

import com.yongjincompany.devblind.dto.user.MyProfileResponse;
import com.yongjincompany.devblind.dto.user.UpdateUserRequest;
import com.yongjincompany.devblind.entity.TechStack;
import com.yongjincompany.devblind.entity.User;
import com.yongjincompany.devblind.entity.UserTechStack;
import com.yongjincompany.devblind.exception.ApiException;
import com.yongjincompany.devblind.exception.ErrorCode;
import com.yongjincompany.devblind.repository.UserRepository;
import com.yongjincompany.devblind.repository.UserTechStackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserTechStackRepository userTechStackRepository;

    public void updateUser(String phoneNumber, UpdateUserRequest request) {
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
    }

    public void deleteUser(String phoneNumber) {
        User user = userRepository.findByPhoneNumberAndDeletedFalse(phoneNumber)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        user.delete();
    }

    public MyProfileResponse getMyProfile(Long userId) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        List<UserTechStack> userStacks = userTechStackRepository.findByUser(user);
        List<TechStack> stacks = userStacks.stream()
                .map(UserTechStack::getTechStack)
                .toList();

        return MyProfileResponse.from(user, stacks);
    }


    public boolean isNicknameDuplicate(String nickname) {
        return userRepository.existsByNicknameAndDeletedFalse(nickname);
    }
}
