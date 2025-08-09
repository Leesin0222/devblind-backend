package com.yongjincompany.devblind.payment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_histories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PaymentHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private Long productId;
    
    @Column(nullable = false)
    private String paymentId;
    
    @Column(nullable = false)
    private String orderId;
    
    @Column(nullable = false)
    private Long amount;
    
    @Column(nullable = false)
    private Integer coin;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RefundStatus refundStatus = RefundStatus.NONE;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime paidAt;
    private LocalDateTime refundedAt;
    private String refundReason;
    
    public enum PaymentStatus {
        PENDING, COMPLETED, FAILED, CANCELLED
    }
    
    public enum RefundStatus {
        NONE, REQUESTED, COMPLETED, FAILED
    }
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    
    public void markSuccess() {
        this.status = PaymentStatus.COMPLETED;
        this.paidAt = LocalDateTime.now();
    }
    
    public void markFailed() {
        this.status = PaymentStatus.FAILED;
    }
    
    public void requestRefund() {
        this.refundStatus = RefundStatus.REQUESTED;
    }
    
    public void completeRefund() {
        this.refundStatus = RefundStatus.COMPLETED;
        this.refundedAt = LocalDateTime.now();
    }
    
    public void failRefund() {
        this.refundStatus = RefundStatus.FAILED;
    }
}
