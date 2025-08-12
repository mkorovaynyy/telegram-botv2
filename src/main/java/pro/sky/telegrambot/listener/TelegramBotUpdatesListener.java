package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        try {
            updates.forEach(update -> {
                logger.info("Processing update: {}", update);

                if (update.message() != null && update.message().text() != null) {
                    String messageText = update.message().text();
                    Long chatId = update.message().chat().id();

                    if ("/start".equals(messageText)) {
                        sendWelcomeMessage(chatId);
                    }
                }
            });
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        } catch (Exception e) {
            logger.error("Error processing updates", e);
            return UpdatesListener.CONFIRMED_UPDATES_NONE;
        }
    }

    private void sendWelcomeMessage(Long chatId) {
        try {
            String welcomeMessage = "🌟 Привет! Я ваш персональный телеграм-бот.\n\n"
                    + "Я создан, чтобы помогать вам в решении различных задач.\n"
                    + "Напишите /help, чтобы увидеть список доступных команд!";

            SendMessage message = new SendMessage(chatId, welcomeMessage);
            telegramBot.execute(message);
            logger.info("Sent welcome message to chat: {}", chatId);
        } catch (Exception e) {
            logger.error("Failed to send welcome message", e);
        }
    }
}