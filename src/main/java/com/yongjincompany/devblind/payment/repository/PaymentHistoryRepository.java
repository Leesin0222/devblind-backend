package com.yongjincompany.devblind.repository;

import com.yongjincompany.devblind.entity.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
    Optional<PaymentHistory> findByOrderId(String orderId);

    List<PaymentHistory> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    List<PaymentHistory> findAllByUserIdAndRefundStatusNot(Long userId, PaymentHistory.RefundStatus refundStatus);
}
