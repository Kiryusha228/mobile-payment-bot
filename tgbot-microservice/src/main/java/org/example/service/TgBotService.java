package org.example.service;

import org.example.client.CrudClient;
import org.example.enums.UserState;
import org.example.model.dto.CreateUserDto;
import org.example.properties.TgBotProperties;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

@Service
public class TgBotService extends TelegramLongPollingBot {
    private final TgBotProperties tgBotProperties;

    private final CrudClient crudClient;

    private final Map<Long, UserState> userStates = new HashMap<>();
    private final Map<Long, String> userContext = new HashMap<>();

    public TgBotService(TgBotProperties tgBotProperties, CrudClient crudClient) {
        super(tgBotProperties.getToken());
        this.tgBotProperties = tgBotProperties;
        this.crudClient = crudClient;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                var text = update.getMessage().getText();
                var chatId = update.getMessage().getChatId();
                var username = update.getMessage().getFrom().getUserName();

                if ("WAIT_SUM".equals(userStates.get(chatId))) {
                    handleAmountInput(chatId, text);
                    return;
                }

                if ("WAIT_PHONE_NUMBER".equals(userStates.get(chatId))) {
                    handlePhoneNumberInput(chatId, text);
                    return;
                }

                switch (text) {
                    case "/start":
                        crudClient.createUser(new CreateUserDto(chatId, username));
                        sendMainMenu(chatId);
                        break;
                    case "Оплатить основной телефон":
                        //payPhone(chatId, );
                        break;
                    case "Список телефонов":
                        sendPhoneList(chatId);
                        break;
                    case "Добавить телефон":
                        sendOperatorChoice(chatId);
                        break;
                    default:
                        execute(SendMessage.builder()
                                .chatId(chatId.toString())
                                .text("Неизвестная команда")
                                .build());
                }
            } else if (update.hasCallbackQuery()) {
                var callbackData = update.getCallbackQuery().getData();
                var chatId = update.getCallbackQuery().getMessage().getChatId();

                if (callbackData.startsWith("pay_")) {
                    payPhone(chatId, callbackData);

                } else if (callbackData.startsWith("operator_")) {
                    handleOperatorChoice(chatId, callbackData);
                }
                else if (callbackData.startsWith("cancel_")) {
                    userStates.remove(chatId);
                    userContext.remove(chatId);

                    execute(SendMessage.builder()
                            .chatId(chatId.toString())
                            .text("Операция отменена")
                            .build());
                }
            }
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMainMenu(Long chatId) throws TelegramApiException {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);

        var rows = new ArrayList<KeyboardRow>();

        rows.add(new KeyboardRow(List.of(new KeyboardButton("Оплатить основной телефон"))));
        rows.add(new KeyboardRow(List.of(new KeyboardButton("Список телефонов"))));
        rows.add(new KeyboardRow(List.of(new KeyboardButton("Добавить телефон"))));

        keyboard.setKeyboard(rows);

        var message = SendMessage.builder()
                .chatId(chatId.toString())
                .text("Выберите действие:")
                .replyMarkup(keyboard)
                .build();

        execute(message);
    }

    private void payPhone(Long chatId, String callbackData) throws TelegramApiException {
        var phoneId = callbackData.replace("pay_", "");
        userStates.put(chatId, UserState.WAIT_SUM);
        userContext.put(chatId, phoneId);

        execute(SendMessage.builder()
                .chatId(chatId.toString())
                .text("Введите сумму для телефона: " + phoneId)
                .build());
    }

    private void sendPhoneList(Long chatId) throws TelegramApiException {
        var markup = new InlineKeyboardMarkup();

        var rows = new ArrayList<List<InlineKeyboardButton>>();
        rows.add(List.of(InlineKeyboardButton.builder()
                .text("📞 9261112233 🟢")
                .callbackData("confirm_phone1")
                .build()));
        rows.add(List.of(InlineKeyboardButton.builder()
                .text("📞 9264445566 🔴")
                .callbackData("confirm_phone2")
                .build()));

        markup.setKeyboard(rows);

        var msg = SendMessage.builder()
                .chatId(chatId.toString())
                .text("Выберите телефон:")
                .replyMarkup(markup)
                .build();

        execute(msg);
    }

    private void sendOperatorChoice(Long chatId) throws TelegramApiException {
        var markup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(List.of(
                InlineKeyboardButton.builder()
                        .text("📡 Beeline")
                        .callbackData("operator_beeline")
                        .build(),
                InlineKeyboardButton.builder()
                        .text("📡 T2")
                        .callbackData("operator_t2")
                        .build()
        ));

        markup.setKeyboard(rows);

        var msg = SendMessage.builder()
                .chatId(chatId.toString())
                .text("Выберите оператора:")
                .replyMarkup(markup)
                .build();

        execute(msg);
    }

    private void handleOperatorChoice(Long chatId, String callbackData) throws TelegramApiException {
        String operator = callbackData.replace("operator_", "");

        userContext.put(chatId, operator);

        userStates.put(chatId, UserState.WAIT_PHONE_NUMBER);

        execute(SendMessage.builder()
                .chatId(chatId.toString())
                .text("Введите номер телефона, без 8")
                .build());
    }

    private void handlePhoneNumberInput(Long chatId, String phoneText) throws TelegramApiException {
        String cleanPhone = phoneText.replaceAll("[^0-9]", "");

        if (cleanPhone.length() < 10 || cleanPhone.length() > 11) {
            execute(SendMessage.builder()
                    .chatId(chatId.toString())
                    .text("❌ Неверный формат номера. Введите 10 цифр.\nПример: 9261234567")
                    .build());
            return;
        }

        String operator = userContext.get(chatId);

        //crudClient.createPhone();

        execute(SendMessage.builder()
                .chatId(chatId.toString())
                .text("✅ Телефон добавлен!\n\n" +
                        "📱 Номер: " + cleanPhone)
                .build());

        userStates.remove(chatId);
        userContext.remove(chatId);

        sendMainMenu(chatId);
    }

    private void handleAmountInput(Long chatId, String amountText) throws TelegramApiException {
        var phoneId = userContext.get(chatId);

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            execute(SendMessage.builder()
                    .chatId(chatId.toString())
                    .text("Введите корректное число")
                    .build());
            return;
        }

        var payUrl = "https://yoomoney.ru/pay?phone=" + phoneId + "&amount=" + amount;

        var markup = new InlineKeyboardMarkup();
        var payBtn = InlineKeyboardButton.builder()
                .text("Оплатить " + amount + " ₽")
                .url(payUrl)
                .build();

        markup.setKeyboard(List.of(List.of(payBtn)));

        var msg = SendMessage.builder()
                .chatId(chatId.toString())
                .text("Нажмите кнопку для перехода к оплате:")
                .replyMarkup(markup)
                .build();

        execute(msg);

        userStates.remove(chatId);
        userContext.remove(chatId);
    }

    @Override
    public String getBotUsername() {
        return tgBotProperties.getName();
    }
}
