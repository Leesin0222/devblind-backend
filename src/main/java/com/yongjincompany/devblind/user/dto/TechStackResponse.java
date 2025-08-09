package com.yongjincompany.devblind.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public record TechStackResponse(
    Long id,
    String name
) {
    public static TechStackResponse from(com.yongjincompany.devblind.user.entity.TechStack stack) {
        return new TechStackResponse(
            stack.getId(),
            stack.getName()
        );
    }
}