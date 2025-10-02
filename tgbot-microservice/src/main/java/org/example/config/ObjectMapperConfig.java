package org.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class ObjectMapperConfig {
    @Bean
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }
}
