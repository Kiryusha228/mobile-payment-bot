package org.example.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "beeline")
public class BeelineProperties {
    private String patternId;
    private String shopId;
    private String showCaseId;
    private String shopArticleId;
}
