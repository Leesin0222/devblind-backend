package com.yongjincompany.devblind.entity;

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

    private String orderId;         // 토스 결제 주문번호
    private Long userId;
    private Long productId;
    private Long amount;            // 결제 금액
    private Long coin;              // 지급 코인 수

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RefundStatus refundStatus = RefundStatus.NONE;

    private LocalDateTime refundRequestedAt;
    private LocalDateTime refundCompletedAt;

    public enum Status {
        PENDING, SUCCESS, FAILED
    }

    public void markSuccess() {
        this.status = Status.SUCCESS;
    }

    public void markFailed() {
        this.status = Status.FAILED;
    }

    public enum RefundStatus {
        NONE, REQUESTED, COMPLETED, FAILED
    }

    public void requestRefund() {
        this.refundStatus = RefundStatus.REQUESTED;
        this.refundRequestedAt = LocalDateTime.now();
    }

    public void completeRefund() {
        this.refundStatus = RefundStatus.COMPLETED;
        this.refundCompletedAt = LocalDateTime.now();
    }

    public void failRefund() {
        this.refundStatus = RefundStatus.FAILED;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
