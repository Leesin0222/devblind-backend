package com.yongjincompany.devblind.payment.entity;

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
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(nullable = false)
    private Long price;
    
    @Column(nullable = false)
    private Long coinAmount;
    
    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
    
    private String description;
    
    public Long getAmount() {
        return this.price;
    }
    
    public Integer getCoin() {
        return this.coinAmount.intValue();
    }
}
