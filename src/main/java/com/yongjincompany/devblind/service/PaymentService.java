package com.yongjincompany.devblind.service;

import com.yongjincompany.devblind.common.TossPaymentClient;
import com.yongjincompany.devblind.dto.PaymentRequest;
import com.yongjincompany.devblind.dto.PaymentResponse;
import com.yongjincompany.devblind.dto.TossWebhookRequest;
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

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentProductRepository productRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final UserRepository userRepository;
    private final TossPaymentClient tossPaymentClient; // 토스 결제 통신 처리
    private final UserBalanceService userBalanceService;

    @Transactional
    public PaymentResponse requestPayment(PaymentRequest request, Long userId) {
        PaymentProduct product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ApiException(ErrorCode.PRODUCT_NOT_FOUND));

        String orderId = UUID.randomUUID().toString();

        // 결제 이력 저장
        PaymentHistory paymentHistory = PaymentHistory.builder()
                .orderId(orderId)
                .userId(userId)
                .productId(product.getId())
                .amount(product.getAmount())
                .coin(product.getCoin())
                .status(PaymentHistory.Status.PENDING)
                .build();

        paymentHistoryRepository.save(paymentHistory);

        String paymentUrl = tossPaymentClient.requestPayment(
                orderId,
                product.getName(),
                product.getAmount(),
                request.successUrl(),
                request.failUrl()
        );

        return new PaymentResponse(paymentUrl);
    }

    @Transactional
    public void handlePaymentResult(TossWebhookRequest request) {
        PaymentHistory paymentHistory = paymentHistoryRepository.findByOrderId(request.orderId())
                .orElseThrow(() -> new RuntimeException("결제 정보가 없습니다."));

        if (paymentHistory.getStatus() == PaymentHistory.Status.SUCCESS) return;

        if (!"DONE".equalsIgnoreCase(request.status())) {
            paymentHistory.markFailed();
            return;
        }

        User user = userRepository.findById(paymentHistory.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));

        userBalanceService.charge(user.getId(), paymentHistory.getCoin()); // 충전 메서드 재사용
        paymentHistory.markSuccess();
    }


}

