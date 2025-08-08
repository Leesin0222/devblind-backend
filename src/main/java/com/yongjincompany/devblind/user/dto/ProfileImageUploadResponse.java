package com.yongjincompany.devblind.dto.user;

import lombok.Builder;
import io.swagger.v3.oas.annotations.media.Schema;

@Builder
@Schema(description = "프로필 이미지 업로드 응답")
public record ProfileImageUploadResponse(
        @Schema(description = "업로드된 이미지 URL", example = "https://devblind-profile-images.s3.ap-northeast-2.amazonaws.com/profile-images/123/abc123.jpg")
        String imageUrl,
        @Schema(description = "응답 메시지", example = "프로필 이미지가 성공적으로 업로드되었습니다.")
        String message
) {}
