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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
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
        log.info("결제 요청: userId={}, productId={}", userId, request.productId());
        
        PaymentProduct product = productRepository.findById(request.productId())
                .orElseThrow(() -> {
                    log.error("결제 상품을 찾을 수 없음: productId={}", request.productId());
                    return new ApiException(ErrorCode.PRODUCT_NOT_FOUND);
                });

        String orderId = UUID.randomUUID().toString();
        log.debug("주문 ID 생성: orderId={}", orderId);

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
        log.debug("결제 이력 저장 완료: orderId={}", orderId);

        String paymentUrl = tossPaymentClient.requestPayment(
                orderId,
                product.getName(),
                product.getAmount(),
                request.successUrl(),
                request.failUrl()
        );

        log.info("결제 URL 생성 완료: userId={}, orderId={}", userId, orderId);
        return new PaymentResponse(paymentUrl);
    }

    @Transactional
    public void handlePaymentResult(TossWebhookRequest request) {
        log.info("결제 결과 처리: orderId={}, status={}", request.orderId(), request.status());
        
        PaymentHistory paymentHistory = paymentHistoryRepository.findByOrderId(request.orderId())
                .orElseThrow(() -> {
                    log.error("결제 정보를 찾을 수 없음: orderId={}", request.orderId());
                    return new ApiException(ErrorCode.PAYMENT_NOT_FOUND);
                });

        if (paymentHistory.getStatus() == PaymentHistory.Status.SUCCESS) {
            log.info("이미 처리된 결제: orderId={}", request.orderId());
            return;
        }

        if (!"DONE".equalsIgnoreCase(request.status())) {
            log.warn("결제 실패: orderId={}, status={}", request.orderId(), request.status());
            paymentHistory.markFailed();
            return;
        }

        User user = userRepository.findById(paymentHistory.getUserId())
                .orElseThrow(() -> {
                    log.error("사용자 정보를 찾을 수 없음: userId={}", paymentHistory.getUserId());
                    return new ApiException(ErrorCode.USER_NOT_FOUND);
                });

        userBalanceService.charge(user.getId(), paymentHistory.getCoin()); // 충전 메서드 재사용
        paymentHistory.markSuccess();
        
        log.info("결제 성공 처리 완료: orderId={}, userId={}, coin={}", 
                request.orderId(), user.getId(), paymentHistory.getCoin());
    }
}

