package com.yongjincompany.devblind.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tech_stacks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TechStack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String name;
    
    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;
}
