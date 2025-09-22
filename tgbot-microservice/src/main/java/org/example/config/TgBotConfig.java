package org.example.config;

import lombok.RequiredArgsConstructor;
import org.example.service.TgBotService;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@RequiredArgsConstructor
public class TgBotConfig {
    private final TgBotService tgBotService;

    @EventListener({ContextRefreshedEvent.class})
    public void init() {
        try {
            var tgApi = new TelegramBotsApi(DefaultBotSession.class);
            tgApi.registerBot(tgBotService);
        } catch (TelegramApiException ex) {
            ex.printStackTrace();
        }
    }
}