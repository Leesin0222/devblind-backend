package com.yongjincompany.devblind.common.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FirebaseConfig {

    private final ResourceLoader resourceLoader;

    @Value("${firebase.service-account.path:classpath:firebase-service-account.json}")
    private String serviceAccountPath;

    @Bean
    @ConditionalOnProperty(name = "firebase.enabled", havingValue = "true", matchIfMissing = false)
    public FirebaseApp firebaseApp() throws IOException {
        log.info("Firebase 초기화 시작: {}", serviceAccountPath);
        
        try {
            InputStream serviceAccount;
            
            if (serviceAccountPath.startsWith("classpath:")) {
                Resource resource = resourceLoader.getResource(serviceAccountPath);
                if (!resource.exists()) {
                    log.warn("Firebase 서비스 계정 파일을 찾을 수 없습니다: {}", serviceAccountPath);
                    return null;
                }
                serviceAccount = resource.getInputStream();
            } else {
                serviceAccount = new FileInputStream(serviceAccountPath);
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp app = FirebaseApp.initializeApp(options);
            log.info("Firebase 초기화 완료");
            return app;
            
        } catch (IOException e) {
            log.error("Firebase 초기화 실패: {}", e.getMessage());
            throw e;
        }
    }
}
