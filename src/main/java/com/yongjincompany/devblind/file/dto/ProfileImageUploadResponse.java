package com.yongjincompany.devblind.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public record ProfileImageUploadResponse(
    String imageUrl,
    String fileName,
    Long fileSize
) {}
