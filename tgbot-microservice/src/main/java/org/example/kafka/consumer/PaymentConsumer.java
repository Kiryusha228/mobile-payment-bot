package org.example.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.example.model.dto.MessagePaymentDto;
import org.example.service.PaymentService;
import org.example.service.TgBotService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PaymentConsumer {
    private final TgBotService tgBotService;
    private final PaymentService paymentService;

    @KafkaListener(topics = "payments", groupId = "payment-group")
    public void consume(MessagePaymentDto messagePaymentDto) throws JsonProcessingException, TelegramApiException {
        var message = "";
        try {
            var yoomoneyPaymentId = paymentService.pay(messagePaymentDto.getPaymentId());
            message = "🎉Оплата прошла!🎉\nОперация в сервисе: " + messagePaymentDto.getPaymentId()
                    + "\nОперация в ЮMoney: " + yoomoneyPaymentId;
        } catch (Exception e) {
            message = "😧Ошибка операции!😢\nОбратитесь к Администрации MobilePay";
            e.printStackTrace();
        }

        tgBotService.executeMessage(message, Objects.requireNonNull(messagePaymentDto.getChatId()));
    }
}
