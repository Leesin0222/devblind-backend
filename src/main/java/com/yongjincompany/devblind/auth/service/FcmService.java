package com.yongjincompany.devblind.auth.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.yongjincompany.devblind.user.repository.DeviceTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

    private final DeviceTokenRepository deviceTokenRepository;

    //필요한 곳에서 해당 메시지 전송 메서드 사용 ex: 매칭 성공! 등등
    public void sendPushMessage(String deviceToken, String title, String body) {
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message message = Message.builder()
                .setToken(deviceToken)
                .setNotification(notification)
                .build();


        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("FCM 메시지 전송 성공: {}", response);
        } catch (FirebaseMessagingException e) {
            // 실패 처리: 무효 토큰이면 DB에서 삭제 등 추가 로직 가능
            log.error("FCM 메시지 전송 실패: {}", e.getMessage());

            // 무효 토큰 에러인 경우 DB에서 삭제
            if (isInvalidTokenError(e)) {
                deviceTokenRepository.findByToken(deviceToken)
                        .ifPresent(deviceTokenRepository::delete);
                log.info("무효한 디바이스 토큰 삭제: {}", deviceToken);
            }
        }
    }


    private boolean isInvalidTokenError(FirebaseMessagingException e) {
        String errorCode = String.valueOf(e.getErrorCode());

        // Firebase에서 무효 토큰 관련 에러 코드 예시
        return "registration-token-not-registered".equals(errorCode) ||
                "invalid-registration-token".equals(errorCode);
    }
}
