package org.example.service;

import org.example.properties.TgBotProperties;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class TgBotService extends TelegramLongPollingBot {
    private final TgBotProperties tgBotProperties;

    public TgBotService(
            TgBotProperties tgBotProperties) {
        super(tgBotProperties.getToken());
        this.tgBotProperties = tgBotProperties;
    }

    @Override
    public void onUpdateReceived(Update update) {
        var message = new SendMessage();
        message.setChatId(update.getMessage().getChatId());
        message.setText("Hello");
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBotUsername() {
        return tgBotProperties.getName();
    }
}
