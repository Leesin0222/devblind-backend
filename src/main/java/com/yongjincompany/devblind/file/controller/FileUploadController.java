package com.yongjincompany.devblind.file.controller;

import com.yongjincompany.devblind.common.security.AuthUser;
import com.yongjincompany.devblind.file.dto.ProfileImageUploadResponse;
import com.yongjincompany.devblind.common.exception.ApiException;
import com.yongjincompany.devblind.file.service.S3StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
@Tag(name = "파일 업로드", description = "프로필 이미지 업로드 API")
public class FileUploadController {

    private final S3StorageService s3StorageService;

    @Operation(
            summary = "프로필 이미지 업로드",
            description = "사용자의 프로필 이미지를 S3에 업로드합니다. 지원 형식: JPG, JPEG, PNG, GIF, WEBP (최대 5MB)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "업로드 성공",
                    content = @Content(schema = @Schema(implementation = ProfileImageUploadResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 파일 형식 또는 크기"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/profile-image")
    public ResponseEntity<ProfileImageUploadResponse> uploadProfileImage(
            @AuthUser Long userId,
            @Parameter(description = "업로드할 이미지 파일", required = true)
            @RequestParam("image") MultipartFile image
    ) {
        log.info("프로필 이미지 업로드 요청: userId={}, fileName={}, size={}", 
                userId, image.getOriginalFilename(), image.getSize());

        try {
            String imageUrl = s3StorageService.uploadProfileImage(image, userId);
            
            ProfileImageUploadResponse response = ProfileImageUploadResponse.builder()
                    .imageUrl(imageUrl)
                    .message("프로필 이미지가 성공적으로 업로드되었습니다.")
                    .build();

            log.info("프로필 이미지 업로드 완료: userId={}, imageUrl={}", userId, imageUrl);
            return ResponseEntity.ok(response);

        } catch (ApiException e) {
            log.warn("프로필 이미지 업로드 실패: userId={}, errorCode={}, message={}", 
                    userId, e.getErrorCode(), e.getMessage());
            throw e; // GlobalExceptionHandler에서 처리

        } catch (Exception e) {
            log.error("프로필 이미지 업로드 중 예상치 못한 오류: userId={}, error={}", userId, e.getMessage());
            throw new ApiException(com.yongjincompany.devblind.common.exception.ErrorCode.SERVER_ERROR);
        }
    }
}
