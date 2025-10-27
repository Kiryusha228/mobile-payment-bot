package org.example.service;

import org.example.client.CrudClient;
import enums.Provider;
import enums.UserState;
import model.dto.ChangeMainPhoneDto;
import model.dto.CreatePhoneDto;
import model.dto.CreateUserDto;
import org.example.properties.TgBotProperties;
import org.example.properties.YoomoneyProperties;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
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

    public TgBotService(
            TgBotProperties tgBotProperties,
            YoomoneyProperties yoomoneyProperties,
            CrudClient crudClient
    ) {
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

                switch (text) {
                    case "/start" -> {
                        crudClient.createUser(new CreateUserDto(chatId, username));
                        sendMainMenu(chatId);
                    }
                    case "Оплатить основной телефон" -> {
                        if (userState != null) {
                            cancelAction(chatId);
                            userState = null;
                        }
                        payMainPhone(chatId);
                    }
                    case "Список телефонов" -> {
                        if (userState != null) {
                            cancelAction(chatId);
                            userState = null;
                        }
                        sendPhoneList(chatId);
                    }
                    case "Добавить телефон" -> {
                        if (userState != null) {
                            cancelAction(chatId);
                            userState = null;
                        }
                        sendProviderChoice(chatId);
                    }
//                    default -> execute(SendMessage.builder()
//                            .chatId(chatId.toString())
//                            .text("🤔 Неизвестная команда, попробуйте снова.")
//                            .build());
                }

                if (userState != null) {
                    switch (userState) {
                        case WAIT_SUM -> handleAmountInput(chatId, text);
                        case WAIT_PHONE_NUMBER -> handlePhoneNumberInput(chatId, text);
                    }
                }

            } else if (update.hasCallbackQuery()) {
                var callbackData = update.getCallbackQuery().getData();
                var chatId = update.getCallbackQuery().getMessage().getChatId();

                if (callbackData.startsWith("pay_")) {
                    payPhone(chatId, callbackData);
                } else if (callbackData.startsWith("provider_")) {
                    handleProviderChoice(update);
                } else if (callbackData.startsWith("main_")) {
                    handleChangeMainPhone(update);
                } else if (callbackData.startsWith("cancel_")) {
                    cancelAction(chatId);
                    editMessage(update, "❌ Действие отменено.", null);
                }
            }

        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void clearUserState(Long chatId) {
        userStates.remove(chatId);
        userContext.remove(chatId);
    }

    private void cancelAction(Long chatId) throws TelegramApiException {
        clearUserState(chatId);
        execute(SendMessage.builder()
                .chatId(chatId.toString())
                .text("❌ Действие отменено.")
                .build());
    }

    private void editMessage(Update update, String text, InlineKeyboardMarkup markup) throws TelegramApiException {
        var msg = update.getCallbackQuery().getMessage();
        var edit = new EditMessageText();
        edit.setChatId(msg.getChatId());
        edit.setMessageId(msg.getMessageId());
        edit.setText(text);
        //markup?.edit.setReplyMarkup(markup)
        if (markup != null) edit.setReplyMarkup(markup);
        execute(edit);
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
                .text("📋 Главное меню:\nВыберите действие:")
                .replyMarkup(keyboard)
                .build();

        execute(message);
    }

    private void sendPhoneList(Long chatId) throws TelegramApiException {
        var phones = crudClient.getPhonesByChatId(chatId);
        if (phones.isEmpty()) {
            execute(SendMessage.builder()
                    .chatId(chatId.toString())
                    .text("📭 Список телефонов пуст.")
                    .build());
            return;
        }

        var rows = new ArrayList<List<InlineKeyboardButton>>();
        for (var phone : phones) {
            rows.add(List.of(
                    InlineKeyboardButton.builder()
                            .text("📞 +7" + phone.getPhoneNumber())
                            .callbackData("pay_" + phone.getId())
                            .build(),
                    InlineKeyboardButton.builder()
                            .text(phone.isMain() ? "🟢" : "⚪️")
                            .callbackData("main_" + phone.getId())
                            .build()
            ));
        }

        var markup = InlineKeyboardMarkup.builder().keyboard(rows).build();

        execute(SendMessage.builder()
                .chatId(chatId.toString())
                .text("📱 Ваши телефоны:")
                .replyMarkup(markup)
                .build());
    }

    private void sendProviderChoice(Long chatId) throws TelegramApiException {
        var markup = new InlineKeyboardMarkup();
        var rows = new ArrayList<List<InlineKeyboardButton>>();
        rows.add(List.of(
                InlineKeyboardButton.builder().text("🐝 Beeline").callbackData("provider_beeline").build(),
                InlineKeyboardButton.builder().text("👨🏻‍🦳 T2").callbackData("provider_t2").build()
        ));
        markup.setKeyboard(rows);

        execute(SendMessage.builder()
                .chatId(chatId.toString())
                .text("📡 Выберите оператора:")
                .replyMarkup(markup)
                .build());
    }

    private void handleProviderChoice(Update update) throws TelegramApiException {
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        var operator = update.getCallbackQuery().getData().replace("provider_", "");
        userContext.put(chatId, operator);
        userStates.put(chatId, UserState.WAIT_PHONE_NUMBER);
        editMessage(update, "📲 Введите номер телефона (10 цифр без 8 или +7):", null);
    }

    private void handlePhoneNumberInput(Long chatId, String phoneText) throws TelegramApiException {
        var cleanPhone = phoneText.replaceAll("[^0-9]", "");
        if (cleanPhone.length() != 10) {
            execute(SendMessage.builder()
                    .chatId(chatId.toString())
                    .text("❌ Неверный формат номера.\nПример: 9261234567")
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
                    .text("✅ Телефон добавлен!\n📱 +7" + phone.getPhoneNumber())
                    .build());
        } catch (Exception e) {
            execute(SendMessage.builder()
                    .chatId(chatId.toString())
                    .text("❌ Ошибка добавления телефона!\n📞 +7" + cleanPhone)
                    .build());
        }

        try {
            crudClient.getMainPhoneByChatId(chatId);
        } catch (Exception ex) {
            crudClient.changeMainPhone(new ChangeMainPhoneDto(chatId, phoneId));
        }

        clearUserState(chatId);
        sendMainMenu(chatId);
    }

    private Provider getEnumProvider(String provider) {
        return switch (provider) {
            case "t2" -> Provider.T2;
            case "beeline" -> Provider.BEELINE;
            default -> throw new IllegalStateException("Неподходящий провайдер: " + provider);
        };
    }

    private void handleAmountInput(Long chatId, String amountText) throws TelegramApiException {
        var phoneId = userContext.get(chatId);
        var userId = crudClient.getUserIdByChatId(chatId);
        double amount;

        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            execute(SendMessage.builder()
                    .chatId(chatId.toString())
                    .text("⚠️ Введите корректное число.")
                    .build());
            return;
        }

        amount += amount * 0.031; // комиссия

        var markup = new InlineKeyboardMarkup();
        var link = String.format(
                yoomoneyProperties.getPaymentLink() + "%s&label=phone%s!user%s",
                amount, phoneId, userId
        );

        var payBtn = InlineKeyboardButton.builder()
                .text("💳 Оплатить " + String.format("%.2f ₽", amount))
                .url(link)
                .build();

        markup.setKeyboard(List.of(List.of(payBtn)));

        execute(SendMessage.builder()
                .chatId(chatId.toString())
                .text("💰 Комиссия сервиса: 3%\nНажмите кнопку ниже для оплаты 👇")
                .replyMarkup(markup)
                .build());

        clearUserState(chatId);
    }

    private void payPhone(Long chatId, String callbackData) throws TelegramApiException {
        var phoneId = callbackData.replace("pay_", "");
        userStates.put(chatId, UserState.WAIT_SUM);
        userContext.put(chatId, phoneId);

        var phone = crudClient.getPhoneById(Long.parseLong(phoneId));
        execute(SendMessage.builder()
                .chatId(chatId.toString())
                .text("💳 Введите сумму пополнения для номера:\n📱 +7" + phone.getPhoneNumber())
                .build());
    }

    private void handleChangeMainPhone(Update update) throws TelegramApiException {
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        var messageId = update.getCallbackQuery().getMessage().getMessageId();
        var phoneId = Long.parseLong(update.getCallbackQuery().getData().replace("main_", ""));

        crudClient.changeMainPhone(new ChangeMainPhoneDto(chatId, phoneId));

        var phones = crudClient.getPhonesByChatId(chatId);
        if (phones.isEmpty()) {
            var edit = new EditMessageText();
            edit.setChatId(chatId);
            edit.setMessageId(messageId);
            edit.setText("📭 Список телефонов пуст.");
            execute(edit);
            return;
        }

        var rows = new ArrayList<List<InlineKeyboardButton>>();
        for (var phone : phones) {
            rows.add(List.of(
                    InlineKeyboardButton.builder()
                            .text("📞 +7" + phone.getPhoneNumber())
                            .callbackData("pay_" + phone.getId())
                            .build(),
                    InlineKeyboardButton.builder()
                            .text(phone.isMain() ? "🟢" : "⚪️")
                            .callbackData("main_" + phone.getId())
                            .build()
            ));
        }

        var markup = InlineKeyboardMarkup.builder().keyboard(rows).build();

        var edit = new EditMessageText();
        edit.setChatId(chatId);
        edit.setMessageId(messageId);
        edit.setText("📱 Ваши телефоны:");
        edit.setReplyMarkup(markup);
        execute(edit);
    }


    private void payMainPhone(Long chatId) throws TelegramApiException {
        try {
            var mainPhone = crudClient.getMainPhoneByChatId(chatId);
            if (mainPhone == null) {
                execute(SendMessage.builder()
                        .chatId(chatId.toString())
                        .text("⚠️ У вас не установлен основной телефон.\nВыберите его в разделе «Список телефонов».")
                        .build());
                return;
            }
            userStates.put(chatId, UserState.WAIT_SUM);
            userContext.put(chatId, String.valueOf(mainPhone.getId()));

            execute(SendMessage.builder()
                    .chatId(chatId.toString())
                    .text("💳 Введите сумму пополнения основного телефона:\n📱 +7" + mainPhone.getPhoneNumber())
                    .build());
        } catch (Exception e) {
            execute(SendMessage.builder()
                    .chatId(chatId.toString())
                    .text("⚠️ Произошла ошибка при получении основного телефона.")
                    .build());
        }
    }

    public void executeMessage(String text, Long chatId) throws TelegramApiException {
        execute(SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .build());
    }

    @Override
    public String getBotUsername() {
        return tgBotProperties.getName();
    }
}
