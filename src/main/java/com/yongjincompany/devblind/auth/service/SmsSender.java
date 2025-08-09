package com.yongjincompany.devblind.auth.service;

public interface SmsSender {
    void send(String phone, String content);
}
