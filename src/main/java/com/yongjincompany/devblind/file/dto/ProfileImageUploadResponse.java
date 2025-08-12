package com.yongjincompany.devblind.file.dto;

public record ProfileImageUploadResponse(
    String imageUrl,
    String fileName,
    Long fileSize
) {}