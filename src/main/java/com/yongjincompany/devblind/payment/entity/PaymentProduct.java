package com.yongjincompany.devblind.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payment_products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PaymentProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // 예: "100코인", "300코인"

    @Column(nullable = false)
    private Long amount; // 실제 결제 금액 (원)

    @Column(nullable = false)
    private Long coin; // 지급될 코인 수

    @Column(nullable = false)
    private boolean active = true;
}
