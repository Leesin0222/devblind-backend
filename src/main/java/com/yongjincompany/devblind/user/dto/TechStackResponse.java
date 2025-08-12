package com.yongjincompany.devblind.user.dto;

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