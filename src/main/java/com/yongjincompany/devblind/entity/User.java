package com.yongjincompany.devblind.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String phoneNumber;

    @Column(nullable = false, length = 20)
    private String nickname;

    @Column(nullable = false)
    private LocalDate birth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Gender gender;

    @Column(nullable = false)
    private String profileImageUrl;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean deleted = false;

    public enum Gender {
        MALE, FEMALE
    }

    public void updateProfile(String nickname, LocalDate birth, Gender gender, String profileImageUrl) {
        this.nickname = nickname;
        this.birth = birth;
        this.gender = gender;
        this.profileImageUrl = profileImageUrl;
    }

    public void delete() {
        this.deleted = true;
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserTechStack> userTechStacks = new ArrayList<>();

    public void setTechStacks(List<TechStack> stacks) {
        this.userTechStacks.clear();
        for (TechStack stack : stacks) {
            this.userTechStacks.add(UserTechStack.builder()
                    .user(this)
                    .techStack(stack)
                    .build());
        }
    }

    @Column(nullable = false)
    private Long balance = 0L;

    public void addBalance(Long amount) {
        this.balance += amount;
    }

    public void deductBalance(Long amount) {
        if (this.balance < amount) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }
        this.balance -= amount;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}



