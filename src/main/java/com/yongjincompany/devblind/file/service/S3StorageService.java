package com.yongjincompany.devblind.file.service;

import com.yongjincompany.devblind.common.exception.ApiException;
import com.yongjincompany.devblind.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3StorageService {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    /**
     * 프로필 이미지를 S3에 업로드
     */
    public String uploadProfileImage(MultipartFile file, Long userId) {
        try {
            // 파일 검증
            validateImageFile(file);

            // 파일명 생성 (중복 방지)
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String fileName = generateFileName(userId, fileExtension);

            // S3에 업로드
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            PutObjectResponse response = s3Client.putObject(putObjectRequest, 
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            if (response.sdkHttpResponse().isSuccessful()) {
                String imageUrl = generateImageUrl(fileName);
                log.info("프로필 이미지 업로드 성공: userId={}, fileName={}, url={}", 
                        userId, fileName, imageUrl);
                return imageUrl;
            } else {
                log.error("프로필 이미지 업로드 실패: userId={}, fileName={}, statusCode={}", 
                        userId, fileName, response.sdkHttpResponse().statusCode());
                throw new ApiException(ErrorCode.FILE_UPLOAD_FAILED);
            }

        } catch (IOException e) {
            log.error("프로필 이미지 업로드 중 IOException 발생: userId={}, error={}", 
                    userId, e.getMessage());
            throw new ApiException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    /**
     * 이미지 파일 검증
     */
    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR);
        }

        // 지원하는 이미지 형식
        String[] allowedContentTypes = {
                "image/jpeg",
                "image/jpg", 
                "image/png",
                "image/gif",
                "image/webp"
        };

        String contentType = file.getContentType();
        if (contentType == null) {
            throw new ApiException(ErrorCode.INVALID_FILE_TYPE);
        }

        // 지원하는 형식인지 확인
        boolean isAllowedType = Arrays.asList(allowedContentTypes).contains(contentType);

        if (!isAllowedType) {
            log.warn("지원하지 않는 파일 형식: contentType={}", contentType);
            throw new ApiException(ErrorCode.INVALID_FILE_TYPE);
        }

        // 파일 크기 제한 (5MB)
        long maxSize = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxSize) {
            throw new ApiException(ErrorCode.FILE_SIZE_EXCEEDED);
        }

        // 파일 확장자 검증
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            String extension = getFileExtension(originalFilename);
            String[] allowedExtensions = {"jpg", "jpeg", "png", "gif", "webp"};
            
            boolean isAllowedExtension = Arrays.asList(allowedExtensions).contains(extension);
            
            if (!isAllowedExtension) {
                log.warn("지원하지 않는 파일 확장자: extension={}", extension);
                throw new ApiException(ErrorCode.INVALID_FILE_TYPE);
            }
        }
    }

    /**
     * 파일 확장자 추출
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "jpg"; // 기본 확장자
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 고유한 파일명 생성
     */
    private String generateFileName(Long userId, String extension) {
        String uuid = UUID.randomUUID().toString();
        return String.format("profile-images/%d/%s.%s", userId, uuid, extension);
    }

    /**
     * 이미지 URL 생성
     */
    private String generateImageUrl(String fileName) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, fileName);
    }
}
