package com.yongjincompany.devblind.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsService {

    private final RedisTemplate<String, String> redisTemplate;
    private final SmsSender smsSender;

    private static final Duration CODE_TTL = Duration.ofMinutes(5);
    private static final String SMS_KEY_PREFIX = "sms:";
    private static final String PROVIDER_NAME = "devBlind";
    private static final String MESSAGE_TEMPLATE = "[%s] 인증번호: [%s] 를 입력해주세요 :D";
    
    private final SecureRandom secureRandom = new SecureRandom();

    public void sendVerificationCode(String phone) {
        log.info("인증 코드 발송 요청: {}", phone);
        
        String verificationCode = generateVerificationCode();
        String message = generateMessage(verificationCode);
        
        // Redis에 코드 저장
        String key = SMS_KEY_PREFIX + phone;
        redisTemplate.opsForValue().set(key, verificationCode, CODE_TTL);
        
        log.debug("인증 코드 저장 완료: {} (TTL: {}분)", phone, CODE_TTL.toMinutes());
        
        // SMS 발송
        try {
            smsSender.send(phone, message);
            log.info("인증 코드 발송 완료: {}", phone);
        } catch (Exception e) {
            log.error("인증 코드 발송 실패: {} - {}", phone, e.getMessage());
            // Redis에서 코드 삭제 (발송 실패 시)
            redisTemplate.delete(key);
            throw e;
        }
    }

    private String generateVerificationCode() {
        return String.format("%06d", secureRandom.nextInt(1000000));
    }

    private String generateMessage(String code) {
        return String.format(MESSAGE_TEMPLATE, PROVIDER_NAME, code);
    }
}
