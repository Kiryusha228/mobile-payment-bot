package org.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.example.client.CrudClient;
import org.example.client.YoomoneyClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final YoomoneyClient yoomoneyClient;
    private final CrudClient crudClient;

    @Retryable(
            retryFor = {Exception.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public String processPaymentWithRetry(String requestId) {
        var processResponse = yoomoneyClient.processRequestPayment(requestId);
        if (!processResponse.getStatus().equals("success")) {
            throw new RuntimeException("Не удалось подтвердить оплату!");
        }
        return processResponse.getRequest_id();
    }

    public String pay(Long paymentId) throws JsonProcessingException {
        var payment = crudClient.getPaymentById(paymentId);
        var phone = crudClient.getPhoneById(payment.getPhoneId());

        var requestResponse = switch (phone.getProvider()) {
            case BEELINE -> yoomoneyClient.requestPaymentBeeline(phone.getPhoneNumber(), String.valueOf(payment.getAmount()));
            case T2 -> yoomoneyClient.requestPaymentT2(phone.getPhoneNumber(), String.valueOf(payment.getAmount()));
        };

        if (!requestResponse.getStatus().equals("success")) {
            throw new RuntimeException("Не удалось запросить оплату!");
        }

        return processPaymentWithRetry(requestResponse.getRequest_id());
    }

    @Recover
    public String recover(Exception e, String requestId) {
        return "failed: " + requestId;
    }
}
