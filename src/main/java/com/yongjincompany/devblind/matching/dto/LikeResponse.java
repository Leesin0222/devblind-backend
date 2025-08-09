package com.yongjincompany.devblind.matching.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public record LikeResponse(
    boolean isMatch,
    String message,
    Long matchingId
) {}
