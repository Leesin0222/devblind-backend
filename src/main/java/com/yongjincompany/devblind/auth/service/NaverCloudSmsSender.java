package com.yongjincompany.devblind.auth.service;

import com.yongjincompany.devblind.auth.config.SmsProperties;
import com.yongjincompany.devblind.common.exception.ApiException;
import com.yongjincompany.devblind.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "sms.provider", havingValue = "ncp")
public class NaverCloudSmsSender implements SmsSender {

    private final WebClient smsWebClient;
    private final SmsProperties smsProperties;

    private static final String HOST = "https://sens.apigw.ntruss.com";
    private static final int MAX_RETRIES = 3;
    private static final Duration RETRY_DELAY = Duration.ofSeconds(1);

    @Override
    public void send(String phone, String content) {
        if (!smsProperties.isEnabled()) {
            log.debug("SMS 기능이 비활성화되어 있습니다.");
            return;
        }

        log.info("SMS 전송 시작: {} (내용 길이: {}자)", phone, content.length());
        
        String uri = "/sms/v2/services/" + smsProperties.getNcp().getServiceId() + "/messages";
        long timestamp = Instant.now().toEpochMilli();
        String signature = makeSignature("POST", uri, timestamp);

        Map<String, Object> body = createRequestBody(phone, content);
        
        log.debug("SMS 요청 바디: {}", body);

        smsWebClient.post()
                .uri(HOST + uri)
                .header("x-ncp-apigw-timestamp", String.valueOf(timestamp))
                .header("x-ncp-iam-access-key", smsProperties.getNcp().getAccessKey())
                .header("x-ncp-apigw-signature-v2", signature)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .toBodilessEntity()
                .retryWhen(Retry.backoff(MAX_RETRIES, RETRY_DELAY)
                        .filter(this::isRetryableError)
                        .doBeforeRetry(retrySignal -> 
                            log.warn("SMS 전송 재시도 {}: {} (에러: {})", 
                                retrySignal.totalRetries() + 1, phone, retrySignal.failure().getMessage())))
                .doOnSuccess(response -> log.info("SMS 전송 성공: {} (상태코드: {})", phone, response.getStatusCode()))
                .doOnError(error -> {
                    log.error("SMS 전송 실패: {} - {}", phone, error.getMessage());
                    if (error instanceof WebClientResponseException webClientError) {
                        log.error("HTTP 상태코드: {}, 응답바디: {}", 
                            webClientError.getStatusCode(), webClientError.getResponseBodyAsString());
                    }
                    throw new ApiException(ErrorCode.SMS_SEND_FAILED);
                })
                .subscribe();
    }

    private Map<String, Object> createRequestBody(String phone, String content) {
        Map<String, Object> body = new HashMap<>();
        body.put("type", "SMS");
        body.put("from", smsProperties.getNcp().getSenderPhone());
        body.put("contentType", "COMM");
        body.put("countryCode", "82");
        body.put("content", content);
        body.put("messages", List.of(Map.of("to", phone)));
        return body;
    }

    private String makeSignature(String method, String url, long timestamp) {
        try {
            String message = String.join("\n", 
                method + " " + url,
                String.valueOf(timestamp),
                smsProperties.getNcp().getAccessKey()
            );

            log.debug("시그니처 생성 - 메시지: {}", message);

            SecretKeySpec signingKey = new SecretKeySpec(
                smsProperties.getNcp().getSecretKey().getBytes(StandardCharsets.UTF_8), 
                "HmacSHA256"
            );
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            String signature = Base64.getEncoder().encodeToString(rawHmac);
            
            log.debug("시그니처 생성 완료: {}", signature);
            return signature;
        } catch (Exception e) {
            log.error("시그니처 생성 실패", e);
            throw new ApiException(ErrorCode.SMS_SEND_FAILED);
        }
    }

    private boolean isRetryableError(Throwable error) {
        if (error instanceof WebClientResponseException webClientError) {
            int statusCode = webClientError.getStatusCode().value();
            log.debug("SMS API 응답 상태코드: {}", statusCode);
            // 5xx 서버 오류나 429 (Too Many Requests)만 재시도
            return statusCode >= 500 || statusCode == 429;
        }
        return false;
    }
}
