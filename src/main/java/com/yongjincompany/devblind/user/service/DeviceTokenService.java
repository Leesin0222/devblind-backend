package com.yongjincompany.devblind.user.service;

import com.yongjincompany.devblind.user.entity.DeviceToken;
import com.yongjincompany.devblind.user.entity.User;
import com.yongjincompany.devblind.common.exception.ApiException;
import com.yongjincompany.devblind.common.exception.ErrorCode;
import com.yongjincompany.devblind.user.repository.DeviceTokenRepository;
import com.yongjincompany.devblind.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceTokenService {

    private final DeviceTokenRepository deviceTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public void registerDeviceToken(Long userId, String token) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        deviceTokenRepository.findByToken(token).ifPresentOrElse(
                existing -> {
                    if (!existing.getUser().getId().equals(userId)) {
                        existing.updateToken(token);
                        existing.setUser(user);
                        deviceTokenRepository.save(existing);
                    }
                    // 이미 같은 유저라면 별도 처리 필요 없을 수 있음
                },
                () -> {
                    DeviceToken newToken = DeviceToken.builder()
                            .token(token)
                            .user(user)
                            .build();
                    deviceTokenRepository.save(newToken);
                }
        );
    }

    public void deleteDeviceToken(String token) {
        deviceTokenRepository.findByToken(token).ifPresent(deviceTokenRepository::delete);
    }
}
