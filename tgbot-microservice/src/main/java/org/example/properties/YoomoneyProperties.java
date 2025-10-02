package org.example.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "yoomoney")
public class YoomoneyProperties {
    private String paymentLink;
    private String token;
    private String requestPaymentLink;
    private String processPaymentLink;
    private String accountInfoLink;
}
