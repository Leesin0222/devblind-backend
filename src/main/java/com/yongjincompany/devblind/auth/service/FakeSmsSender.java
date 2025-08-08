package com.yongjincompany.devblind.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(name = "sms.provider", havingValue = "fake")
public class FakeSmsSender implements SmsSender {

    @Override
    public void send(String phone, String content) {
        log.info("FAKE SMS 전송: {} → {}", phone, content);
    }
}
