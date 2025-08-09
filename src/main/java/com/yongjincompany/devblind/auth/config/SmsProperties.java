package com.yongjincompany.devblind.auth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "sms")
public class SmsProperties {
    
    private boolean enabled = true;
    private Ncp ncp = new Ncp();
    
    @Getter
    @Setter
    public static class Ncp {
        private String accessKey;
        private String secretKey;
        private String serviceId;
        private String senderPhone;
    }
}
