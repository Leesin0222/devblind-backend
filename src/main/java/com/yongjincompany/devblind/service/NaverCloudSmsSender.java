package com.yongjincompany.devblind.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NaverCloudSmsSender {

    @Value("${sms.ncp.access-key}")
    private String accessKey;

    @Value("${sms.ncp.secret-key}")
    private String secretKey;

    @Value("${sms.ncp.service-id}")
    private String serviceId;

    @Value("${sms.ncp.sender-phone}")
    private String senderPhone;

    public void send(String phone, String content) {
        // 실제 NCP SMS API 요청 로직
        // HTTP POST 요청 보내는 방식 (RestTemplate or WebClient)
        // 실제 구현은 2단계 끝나고 3단계에서 작성
        System.out.println(">> SMS 전송: " + phone + " → " + content);
    }
}
