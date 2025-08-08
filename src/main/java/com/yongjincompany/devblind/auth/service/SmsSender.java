package com.yongjincompany.devblind.service;

public interface SmsSender {
    void send(String phone, String content);
}
