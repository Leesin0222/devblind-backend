package com.yongjincompany.devblind.service;

import com.yongjincompany.devblind.common.TossRefundClient;
import com.yongjincompany.devblind.dto.RefundHistoryResponse;
import com.yongjincompany.devblind.dto.RefundRequest;
import com.yongjincompany.devblind.dto.TossRefundWebhookRequest;
import com.yongjincompany.devblind.entity.PaymentHistory;
import com.yongjincompany.devblind.entity.PaymentProduct;
import com.yongjincompany.devblind.entity.User;
import com.yongjincompany.devblind.exception.ApiException;
import com.yongjincompany.devblind.exception.ErrorCode;
import com.yongjincompany.devblind.repository.PaymentHistoryRepository;
import com.yongjincompany.devblind.repository.PaymentProductRepository;
import com.yongjincompany.devblind.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefundService {

    private final UserRepository userRepository;
    private final PaymentProductRepository paymentProductRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final TossRefundClient tossRefundClient; // 토스 환불 API 연동 클라이언트
    private final UserBalanceService userBalanceService;

    @Transactional
    public void requestRefund(RefundRequest request, Long userId) {
        log.info("환불 요청: userId={}, orderId={}, reason={}", userId, request.orderId(), request.reason());
        
        PaymentHistory payment = paymentHistoryRepository.findByOrderId(request.orderId())
                .orElseThrow(() -> {
                    log.error("결제 정보를 찾을 수 없음: orderId={}", request.orderId());
                    return new ApiException(ErrorCode.PAYMENT_NOT_FOUND);
                });

        if (!payment.getUserId().equals(userId)) {
            log.error("환불 권한 없음: requestUserId={}, paymentUserId={}", userId, payment.getUserId());
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }

        if (payment.getRefundStatus() != PaymentHistory.RefundStatus.NONE) {
            log.error("이미 환불 처리된 결제: orderId={}, refundStatus={}", request.orderId(), payment.getRefundStatus());
            throw new ApiException(ErrorCode.INVALID_REFUND_REQUEST);
        }

        // 토스 환불 API 호출
        boolean refundResult = tossRefundClient.refund(payment.getOrderId(), payment.getAmount(), request.reason());

        if (refundResult) {
            payment.requestRefund();
            log.info("환불 요청 성공: orderId={}", request.orderId());
            // 환불 완료 시 별도 webhook 처리 필요 시 상태 업데이트 가능
        } else {
            log.error("환불 요청 실패: orderId={}", request.orderId());
            throw new ApiException(ErrorCode.REFUND_FAILED);
        }
    }

    @Transactional
    public void handleRefundResult(TossRefundWebhookRequest request) {
        log.info("환불 결과 처리: orderId={}, status={}", request.orderId(), request.status());
        
        PaymentHistory paymentHistory = paymentHistoryRepository.findByOrderId(request.orderId())
                .orElseThrow(() -> {
                    log.error("결제 정보를 찾을 수 없음: orderId={}", request.orderId());
                    return new ApiException(ErrorCode.PAYMENT_NOT_FOUND);
                });

        if ("CANCELED".equalsIgnoreCase(request.status())) {
            paymentHistory.completeRefund();

            // 유저 코인 차감 로직 필요하면 여기서 처리
            User user = userRepository.findById(paymentHistory.getUserId())
                    .orElseThrow(() -> {
                        log.error("사용자 정보를 찾을 수 없음: userId={}", paymentHistory.getUserId());
                        return new ApiException(ErrorCode.USER_NOT_FOUND);
                    });

            userBalanceService.spend(user.getId(), paymentHistory.getCoin());
            log.info("환불 완료 처리: orderId={}, userId={}, coin={}", 
                    request.orderId(), user.getId(), paymentHistory.getCoin());
        } else {
            paymentHistory.failRefund();
            log.warn("환불 실패: orderId={}, status={}", request.orderId(), request.status());
        }
    }
    
    public List<RefundHistoryResponse> getUserRefundHistories(Long userId) {
        log.debug("환불 내역 조회: userId={}", userId);
        
        List<PaymentHistory> histories = paymentHistoryRepository.findAllByUserIdAndRefundStatusNot(userId, PaymentHistory.RefundStatus.NONE);

        Map<Long, String> productMap = paymentProductRepository.findAllById(
                histories.stream().map(PaymentHistory::getProductId).toList()
        ).stream().collect(Collectors.toMap(PaymentProduct::getId, PaymentProduct::getName));

        List<RefundHistoryResponse> responses = histories.stream()
                .map(h -> RefundHistoryResponse.from(h, productMap.getOrDefault(h.getProductId(), "Unknown")))
                .toList();
                
        log.debug("환불 내역 조회 완료: userId={}, count={}", userId, responses.size());
        return responses;
    }
}
