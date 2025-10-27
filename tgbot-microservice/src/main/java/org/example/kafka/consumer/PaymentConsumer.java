package org.example.kafka.consumer;

import lombok.RequiredArgsConstructor;
import model.dto.MessagePaymentDto;
import org.example.service.PaymentService;
import org.example.service.TgBotService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PaymentConsumer {
    private final TgBotService tgBotService;
    private final PaymentService paymentService;

    @KafkaListener(topics = "payments", groupId = "payment-group")
    public void consume(MessagePaymentDto messagePaymentDto) {
        try {
            var chatId = Objects.requireNonNull(messagePaymentDto.getChatId());
            tgBotService.executeMessage("😉 Деньги получены!\nПытаемся положить их к тебе на счёт 😏", chatId);

            var yoomoneyPaymentId = paymentService.pay(messagePaymentDto.getPaymentId());
            String message;

            // todo: stringbuilder

            if (yoomoneyPaymentId.startsWith("failed:")) {
                message = "😧 Оплата не прошла после нескольких попыток!\nПопробуйте позже или обратитесь в поддержку.\n"
                        + yoomoneyPaymentId;
            } else {
                message = "🎉 Оплата прошла успешно! 🎉\nОперация в ЮMoney: " + yoomoneyPaymentId;
            }

            message += "\nОперация в сервисе: " + messagePaymentDto.getPaymentId();
            tgBotService.executeMessage(message, chatId);

        } catch (Exception e) {
            e.printStackTrace();
            try {
                tgBotService.executeMessage("😢 Произошла ошибка при оплате, обратитесь в поддержку."
                        + "\nОперация в сервисе: " + messagePaymentDto.getPaymentId(), Objects.requireNonNull(messagePaymentDto.getChatId()));
            } catch (Exception ignored) {}
        }
    }
}
