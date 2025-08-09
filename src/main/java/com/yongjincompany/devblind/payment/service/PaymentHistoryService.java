package com.yongjincompany.devblind.payment.service;

import com.yongjincompany.devblind.payment.dto.PaymentHistoryResponse;
import com.yongjincompany.devblind.payment.entity.PaymentHistory;
import com.yongjincompany.devblind.payment.entity.PaymentProduct;
import com.yongjincompany.devblind.payment.repository.PaymentHistoryRepository;
import com.yongjincompany.devblind.payment.repository.PaymentProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentHistoryService {

    private final PaymentHistoryRepository paymentHistoryRepository;
    private final PaymentProductRepository paymentProductRepository;

    public List<PaymentHistoryResponse> getPaymentHistories(Long userId) {
        List<PaymentHistory> histories = paymentHistoryRepository.findAllByUserIdOrderByCreatedAtDesc(userId);

        // 상품명 매핑
        Map<Long, String> productMap = paymentProductRepository.findAllById(
                histories.stream().map(PaymentHistory::getProductId).toList()
        ).stream().collect(Collectors.toMap(PaymentProduct::getId, PaymentProduct::getName));

        return histories.stream()
                .map(h -> PaymentHistoryResponse.from(h, productMap.getOrDefault(h.getProductId(), "Unknown")))
                .toList();
    }
}
