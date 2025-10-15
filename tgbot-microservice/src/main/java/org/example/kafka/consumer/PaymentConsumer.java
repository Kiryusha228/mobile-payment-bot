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
        tgBotService.executeMessage("😉 Деньги получены!\n Пытаемся положить их к тебе на счет 😏", Objects.requireNonNull(messagePaymentDto.getChatId()));

        var message = "";
        var yoomoneyPaymentId = paymentService.pay(messagePaymentDto.getPaymentId());
        if (yoomoneyPaymentId.startsWith("failed:")) {
            message = "😧 Оплата не прошла после нескольких попыток!\nПопробуйте позже или обратитесь к поддержке\n"
                    + yoomoneyPaymentId;
        } else {
            message = "🎉 Оплата прошла успешно! 🎉\nОперация в ЮMoney: " + yoomoneyPaymentId;
        }

        message += "\nОперация в сервисе: " + messagePaymentDto.getPaymentId();

        tgBotService.executeMessage(message, Objects.requireNonNull(messagePaymentDto.getChatId()));
    }
}
