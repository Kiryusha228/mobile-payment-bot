package org.example.kafka.consumer;

import lombok.RequiredArgsConstructor;
import org.example.model.dto.MessagePaymentDto;
import org.example.service.TgBotService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@RequiredArgsConstructor
public class PaymentConsumer {
    private final TgBotService tgBotService;

    @KafkaListener(topics = "payments", groupId = "payment-group")
    public void consume(MessagePaymentDto messagePaymentDto) {
        var message = new SendMessage();
        message.setChatId(messagePaymentDto.getChatId());
        message.setText("Оплата прошла! " + messagePaymentDto.getPaymentId());

        try {
            tgBotService.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
