package org.example.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "t2")
public class T2Properties {
    private String patternId;
    private String a3RecipientId;
    private String shopId;
    private String showCaseId;
    private String shopArticleId;
}
