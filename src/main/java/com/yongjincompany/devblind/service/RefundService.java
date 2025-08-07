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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        PaymentHistory payment = paymentHistoryRepository.findByOrderId(request.orderId())
                .orElseThrow(() -> new ApiException(ErrorCode.PAYMENT_NOT_FOUND));

        if (!payment.getUserId().equals(userId)) {
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }

        if (payment.getRefundStatus() != PaymentHistory.RefundStatus.NONE) {
            throw new ApiException(ErrorCode.INVALID_REFUND_REQUEST);
        }

        // 토스 환불 API 호출
        boolean refundResult = tossRefundClient.refund(payment.getOrderId(), payment.getAmount(), request.reason());

        if (refundResult) {
            payment.requestRefund();
            // 환불 완료 시 별도 webhook 처리 필요 시 상태 업데이트 가능
        } else {
            throw new ApiException(ErrorCode.REFUND_FAILED);
        }
    }

    @Transactional
    public void handleRefundResult(TossRefundWebhookRequest request) {
        PaymentHistory paymentHistory = paymentHistoryRepository.findByOrderId(request.orderId())
                .orElseThrow(() -> new RuntimeException("결제 정보가 없습니다."));

        if ("CANCELED".equalsIgnoreCase(request.status())) {
            paymentHistory.completeRefund();

            // 유저 코인 차감 로직 필요하면 여기서 처리
            User user = userRepository.findById(paymentHistory.getUserId())
                    .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));

            userBalanceService.deduct(user.getId(), paymentHistory.getCoin());
        } else {
            paymentHistory.failRefund();
        }
    }
    public List<RefundHistoryResponse> getUserRefundHistories(Long userId) {
        List<PaymentHistory> histories = paymentHistoryRepository.findAllByUserIdAndRefundStatusNot(userId, PaymentHistory.RefundStatus.NONE);

        Map<Long, String> productMap = paymentProductRepository.findAllById(
                histories.stream().map(PaymentHistory::getProductId).toList()
        ).stream().collect(Collectors.toMap(PaymentProduct::getId, PaymentProduct::getName));

        return histories.stream()
                .map(h -> RefundHistoryResponse.from(h, productMap.getOrDefault(h.getProductId(), "Unknown")))
                .toList();
    }



}
