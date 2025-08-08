package com.yongjincompany.devblind.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 인증 관련
    UNAUTHORIZED("UNAUTHORIZED", "인증이 필요합니다."),
    INVALID_TOKEN("INVALID_TOKEN", "유효하지 않은 토큰입니다."),
    INVALID_REFRESH_TOKEN("INVALID_REFRESH_TOKEN", "유효하지 않은 리프레시 토큰입니다."),
    INVALID_CODE("INVALID_CODE", "유효하지 않은 인증 코드입니다."),
    
    // 사용자 관련
    USER_NOT_FOUND("USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
    VALIDATION_ERROR("VALIDATION_ERROR", "입력값이 올바르지 않습니다."),
    
    // 파일 업로드 관련
    FILE_UPLOAD_FAILED("FILE_UPLOAD_FAILED", "파일 업로드에 실패했습니다."),
    INVALID_FILE_TYPE("INVALID_FILE_TYPE", "지원하지 않는 파일 형식입니다. (JPG, JPEG, PNG, GIF, WEBP만 지원)"),
    FILE_SIZE_EXCEEDED("FILE_SIZE_EXCEEDED", "파일 크기가 제한을 초과했습니다. (최대 5MB)"),
    
    // 결제 관련
    INSUFFICIENT_COINS("INSUFFICIENT_COINS", "코인이 부족합니다."),
    ADDITIONAL_RECOMMENDATION_LIMIT_EXCEEDED("ADDITIONAL_RECOMMENDATION_LIMIT_EXCEEDED", "오늘 추가 추천 사용 횟수를 초과했습니다."),
    
    // 채팅 관련
    CHAT_ROOM_NOT_FOUND("CHAT_ROOM_NOT_FOUND", "채팅방을 찾을 수 없습니다."),
    CHAT_MESSAGE_NOT_FOUND("CHAT_MESSAGE_NOT_FOUND", "채팅 메시지를 찾을 수 없습니다."),
    
    // 매칭 관련
    MATCHING_NOT_FOUND("MATCHING_NOT_FOUND", "매칭을 찾을 수 없습니다."),
    
    // 서버 오류
    SERVER_ERROR("SERVER_ERROR", "서버 오류가 발생했습니다.");

    private final String code;
    private final String message;
}
