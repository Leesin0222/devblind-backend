package com.yongjincompany.devblind.service;

import com.yongjincompany.devblind.dto.PaymentHistoryResponse;
import com.yongjincompany.devblind.entity.PaymentHistory;
import com.yongjincompany.devblind.entity.PaymentProduct;
import com.yongjincompany.devblind.repository.PaymentHistoryRepository;
import com.yongjincompany.devblind.repository.PaymentProductRepository;
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

    public List<PaymentHistoryResponse> getUserPaymentHistories(Long userId) {
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
