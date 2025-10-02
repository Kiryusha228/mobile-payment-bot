package org.example.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.config.ObjectMapperConfig;
import org.example.config.WebClientConfig;
import org.example.model.dto.PaymentResponseDto;
import org.example.properties.BeelineProperties;
import org.example.properties.T2Properties;
import org.example.properties.YoomoneyProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class YoomoneyClient {
    private final WebClientConfig webClient;
    private final ObjectMapperConfig objectMapper;

    private final YoomoneyProperties yoomoneyProperties;
    private final T2Properties t2Properties;
    private final BeelineProperties beelineProperties;

    public String requestPaymentT2(String phone, String sum) throws JsonProcessingException {
        var response = webClient.getWebClient()
                .post()
                .uri(yoomoneyProperties.getRequestPaymentLink())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + yoomoneyProperties.getToken())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(
                        "pattern_id=" + t2Properties.getPatternId() +
                                "&topped_up_phone=" + phone +
                                "&sum=" + sum +
                                "&a3RecipientId="+ t2Properties.getA3RecipientId() +
                                "&ShopID=" + t2Properties.getShopId() +
                                "&ShowCaseID=" + t2Properties.getShowCaseId() +
                                "&ShopArticleID=" + t2Properties.getShopArticleId()
                )
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return objectMapper.getObjectMapper().readTree(response).get("request_id").asText();
    }

    public String requestPaymentBeeline(String phone, String sum) throws JsonProcessingException {
        var response = webClient.getWebClient()
                .post()
                .uri(yoomoneyProperties.getRequestPaymentLink())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + yoomoneyProperties.getToken())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(
                        "pattern_id=" + beelineProperties.getPatternId() +
                                "&PROPERTY1=" + phone +
                                "&netSum=" + sum +
                                "&ShopID=" + beelineProperties.getShopId() +
                                "&ShowCaseID=" + beelineProperties.getShowCaseId() +
                                "&ShopArticleID=" + beelineProperties.getShopArticleId()
                )
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return objectMapper.getObjectMapper().readTree(response).get("request_id").asText();
    }

    public PaymentResponseDto processRequestPayment(String requestId) {
        return webClient.getWebClient()
                .post()
                .uri(yoomoneyProperties.getProcessPaymentLink())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + yoomoneyProperties.getToken())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("request_id=" + requestId)
                .retrieve()
                .bodyToMono(PaymentResponseDto.class)
                .block();
    }

    public String getAccountBalance() throws JsonProcessingException {
        var response = webClient.getWebClient()
                .get()
                .uri(yoomoneyProperties.getAccountInfoLink())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + yoomoneyProperties.getToken())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return objectMapper.getObjectMapper().readTree(response).get("balance").asText();
    }
}
