package org.example.config;

import lombok.RequiredArgsConstructor;
import org.example.properties.ApiProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {
    private final ApiProperties apiProperties;

    @Bean
    public WebClient getWebClient() {
        return WebClient.builder().baseUrl(apiProperties.getCrudMicroserviceUrl()).build();
    }
}