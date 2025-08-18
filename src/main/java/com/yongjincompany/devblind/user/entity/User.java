package com.yongjincompany.devblind.user.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Builder.Default
    @Column(nullable = false)
    private boolean deleted = false;

    @Column
    private String bio;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private String location;

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

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<com.yongjincompany.devblind.user.entity.UserTechStack> userTechStacks = new ArrayList<>();

    public void setTechStacks(List<com.yongjincompany.devblind.user.entity.TechStack> stacks) {
        this.userTechStacks.clear();
        for (com.yongjincompany.devblind.user.entity.TechStack stack : stacks) {
            this.userTechStacks.add(com.yongjincompany.devblind.user.entity.UserTechStack.builder()
                    .user(this)
                    .techStack(stack)
                    .build());
        }
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}



