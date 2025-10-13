package org.example.service;

import org.example.client.CrudClient;
import org.example.enums.Provider;
import org.example.enums.UserState;
import org.example.model.dto.ChangeMainPhoneDto;
import org.example.model.dto.CreatePhoneDto;
import org.example.model.dto.CreateUserDto;
import org.example.model.entity.PhoneEntity;
import org.example.properties.TgBotProperties;
import org.example.properties.YoomoneyProperties;
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
    private final YoomoneyProperties yoomoneyProperties;

    private final CrudClient crudClient;

    private final Map<Long, UserState> userStates = new HashMap<>();
    private final Map<Long, String> userContext = new HashMap<>();

    public TgBotService(TgBotProperties tgBotProperties, YoomoneyProperties yoomoneyProperties, CrudClient crudClient) {
        super(tgBotProperties.getToken());
        this.tgBotProperties = tgBotProperties;
        this.yoomoneyProperties = yoomoneyProperties;
        this.crudClient = crudClient;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                var text = update.getMessage().getText();
                var chatId = update.getMessage().getChatId();
                var username = update.getMessage().getFrom().getUserName();
                var userState = userStates.get(chatId);

                if (userState != null) {
                    switch (userState) {
                        case WAIT_SUM -> handleAmountInput(chatId, text);
                        case WAIT_PHONE_NUMBER -> handlePhoneNumberInput(chatId, text);
                    }
                    return;
                }

                switch (text) {
                    case "/start":
                        try {
                            crudClient.createUser(new CreateUserDto(chatId, username));
                        } finally {
                            sendMainMenu(chatId);
                        }
                        break;
                    case "Оплатить основной телефон":
                        //payPhone(chatId, );
                        break;
                    case "Список телефонов":
                        sendPhoneList(chatId);
                        break;
                    case "Добавить телефон":
                        sendProviderChoice(chatId);
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
                } else if (callbackData.startsWith("provider_")) {
                    handleProviderChoice(chatId, callbackData);
                } else if (callbackData.startsWith("main_")) {
                    handleChangeMainPhone(chatId, callbackData);
                } else if (callbackData.startsWith("cancel_")) {
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

        var phone = crudClient.getPhoneById(Long.parseLong(phoneId));

        execute(SendMessage.builder()
                .chatId(chatId.toString())
                .text("Введите сумму для пополнения телефона: +7" + phone.getPhoneNumber())
                .build());
    }

    private void sendPhoneList(Long chatId) throws TelegramApiException {
        var phones = crudClient.getPhonesByChatId(chatId);

        if (phones.isEmpty()) {
            var msg = SendMessage.builder()
                    .chatId(chatId.toString())
                    .text("Список телефонов пуст!")
                    .build();
            execute(msg);
            return;
        }

        var markup = new InlineKeyboardMarkup();

        var rows = new ArrayList<List<InlineKeyboardButton>>();

        for (var phone : phones) {
            rows.add(
                    List.of(
                            InlineKeyboardButton.builder()
                                    .text("📞 +7" + phone.getPhoneNumber())
                                    .callbackData("pay_" + phone.getId())
                                    .build(),
                            InlineKeyboardButton.builder()
                                    .text(phone.getMain() ? "🟢" : "🔴")
                                    .callbackData("main_" + phone.getId())
                                    .build()
                    )
            );
        }

        markup.setKeyboard(rows);

        var msg = SendMessage.builder()
                .chatId(chatId.toString())
                .text("Выберите телефон:")
                .replyMarkup(markup)
                .build();

        execute(msg);
    }

    private void sendProviderChoice(Long chatId) throws TelegramApiException {
        var markup = new InlineKeyboardMarkup();

        var rows = new ArrayList<List<InlineKeyboardButton>>();
        rows.add(List.of(
                InlineKeyboardButton.builder()
                        .text("🐝Beeline")
                        .callbackData("provider_beeline")
                        .build(),
                InlineKeyboardButton.builder()
                        .text("👨🏻‍🦳T2")
                        .callbackData("provider_t2")
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

    private void handleProviderChoice(Long chatId, String callbackData) throws TelegramApiException {
        var operator = callbackData.replace("provider_", "");

        userContext.put(chatId, operator);

        userStates.put(chatId, UserState.WAIT_PHONE_NUMBER);

        execute(SendMessage.builder()
                .chatId(chatId.toString())
                .text("Введите номер телефона, без 8(+7)")
                .build());
    }

    private void handlePhoneNumberInput(Long chatId, String phoneText) throws TelegramApiException {
        var cleanPhone = phoneText.replaceAll("[^0-9]", "");

        if (cleanPhone.length() != 10) {
            execute(SendMessage.builder()
                    .chatId(chatId.toString())
                    .text("❌ Неверный формат номера. Введите 10 цифр.\nПример: 9261234567")
                    .build());
            return;
        }

        var provider = getEnumProvider(userContext.get(chatId));
        var userId = crudClient.getUserIdByChatId(chatId);
        Long phoneId = null;

        try {
            var phone = crudClient.createPhone(new CreatePhoneDto(userId, cleanPhone, provider));
            phoneId = phone.getId();
            execute(SendMessage.builder()
                    .chatId(chatId.toString())
                    .text("✅ Телефон добавлен!\n\n" +
                            "📱 Номер: +7" + phone.getPhoneNumber())
                    .build());
        } catch (Exception e) {
            execute(SendMessage.builder()
                    .chatId(chatId.toString())
                    .text("❌ Ошибка добавления телефона!\n\n" +
                            "📱 Номер: +7" + cleanPhone)
                    .build());
            e.printStackTrace();
        }

        try {
            crudClient.getMainPhoneByChatId(chatId);
        } catch (Exception ex) {
            crudClient.changeMainPhone(new ChangeMainPhoneDto(chatId, phoneId));
        }

        userStates.remove(chatId);
        userContext.remove(chatId);

        sendMainMenu(chatId);
    }

    private Provider getEnumProvider(String provider) {
        return switch (provider) {
            case "t2" -> Provider.T2;
            case "beeline" -> Provider.BEELINE;
            default -> throw new IllegalStateException("Неподходящий провайдер:" + provider);
        };
    }

    private void handleAmountInput(Long chatId, String amountText) throws TelegramApiException {
        var phoneId = userContext.get(chatId);
        var userId = crudClient.getUserIdByChatId(chatId);
        //var userId =

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

        amount += amount * 0.031; //Проценты

        var markup = new InlineKeyboardMarkup();

        var link = String.format(yoomoneyProperties.getPaymentLink() +"%s&label=phone%s#user%s", amount, phoneId, userId);

        var payBtn = InlineKeyboardButton.builder()
                .text("Оплатить " + amount + " ₽")
                .url(link)
                .build();

        markup.setKeyboard(List.of(List.of(payBtn)));

        var msg = SendMessage.builder()
                .chatId(chatId.toString())
                .text("Нажмите кнопку для перехода к оплате (Комиссия сервиса 3%):")
                .replyMarkup(markup)
                .build();

        execute(msg);

        userStates.remove(chatId);
        userContext.remove(chatId);
    }

    private void handleChangeMainPhone(Long chatId, String callbackData) throws TelegramApiException {
        var phoneId = Long.parseLong(callbackData.replace("main_", ""));
        crudClient.changeMainPhone(new ChangeMainPhoneDto(chatId, phoneId));
        sendPhoneList(chatId);
    }

    @Override
    public String getBotUsername() {
        return tgBotProperties.getName();
    }
}
