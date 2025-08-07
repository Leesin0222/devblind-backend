package com.yongjincompany.devblind.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SmsService {

    private final RedisTemplate<String, String> redisTemplate;
    private final NaverCloudSmsSender smsSender;

    private static final Duration CODE_TTL = Duration.ofMinutes(5);

    public void sendVerificationCode(String phone) {
        String varificationCode = generateVarificationCode();

        redisTemplate.opsForValue().set("sms:" + phone, varificationCode, CODE_TTL);
        smsSender.send(phone, generateMessage(varificationCode));
    }

    private String generateVarificationCode() {
        return String.valueOf((int)(Math.random() * 900000) + 100000); // 6자리
    }

    private String generateMessage(String code) {
        final String provider = "devBlind";
        return "[" + provider + "] 인증번호: [" + code + "] 를 입력해주세요 :D";
    }
}
