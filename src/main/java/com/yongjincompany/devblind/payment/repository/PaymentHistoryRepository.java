package com.yongjincompany.devblind.payment.repository;

import com.yongjincompany.devblind.payment.entity.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
    
    List<PaymentHistory> findAllByUserIdOrderByCreatedAtDesc(Long userId);
    
    Optional<PaymentHistory> findByPaymentId(String paymentId);
    
    List<PaymentHistory> findByUserIdAndRefundStatus(Long userId, PaymentHistory.RefundStatus refundStatus);
    
    Optional<PaymentHistory> findByOrderId(String orderId);
    
    List<PaymentHistory> findAllByUserIdAndRefundStatusNot(Long userId, PaymentHistory.RefundStatus refundStatus);
}
