package com.yongjincompany.devblind.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentKey;

    private String orderId;

    private Long userId;

    private Long amount;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        PENDING, SUCCESS, FAILED
    }

    public void markSuccess() {
        this.status = Status.SUCCESS;
    }

    public void markFailed() {
        this.status = Status.FAILED;
    }
}
