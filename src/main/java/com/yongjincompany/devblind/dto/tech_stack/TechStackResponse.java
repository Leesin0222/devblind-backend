package com.yongjincompany.devblind.dto.tech_stack;

import com.yongjincompany.devblind.entity.TechStack;

public record TechStackResponse(Long id, String name) {
    public static TechStackResponse from(TechStack stack) {
        return new TechStackResponse(stack.getId(), stack.getName());
    }
}