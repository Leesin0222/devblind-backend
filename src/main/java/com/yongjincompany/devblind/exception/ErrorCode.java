package com.yongjincompany.devblind.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    PRODUCT_NOT_FOUND(404, "결제 상품을 찾을 수 없습니다."),
    INVALID_REFUND_REQUEST(400, "유효하지 않은 환불 요청입니다."),
    REFUND_FAILED(500, "환불 처리에 실패했습니다."),
    PAYMENT_NOT_FOUND(404, "결제 정보를 찾을 수 없습니다."),
    INVALID_REFRESH_TOKEN(401, "유효하지 않은 리프레시 토큰입니다."),
    INVALID_TOKEN(401, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(401, "토큰이 만료되었습니다."),
    UNAUTHORIZED(401, "인증이 필요합니다."),
    FORBIDDEN(403, "권한이 없습니다."),
    INVALID_CODE(400, "인증번호가 일치하지 않습니다."),
    USER_NOT_FOUND(404, "사용자를 찾을 수 없습니다."),
    VALIDATION_ERROR(400, "요청 값이 올바르지 않습니다."),
    SERVER_ERROR(500, "서버 오류가 발생했습니다.");

    private final int status;
    private final String message;
}

