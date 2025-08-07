package com.yongjincompany.devblind.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "user_balances")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserBalance {

    @Id
    private Long userId; // PK, FK User.id

    @Column(nullable = false)
    private Long balance;

    public void charge(long amount) {
        if (amount <= 0) throw new IllegalArgumentException("충전 금액은 양수여야 합니다.");
        this.balance += amount;
    }

    public void spend(long amount) {
        if (amount <= 0) throw new IllegalArgumentException("사용 금액은 양수여야 합니다.");
        if (balance < amount) throw new IllegalStateException("잔액이 부족합니다.");
        this.balance -= amount;
    }
}
