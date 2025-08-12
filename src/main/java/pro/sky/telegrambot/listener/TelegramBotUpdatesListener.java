package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.exception.InvalidTaskFormatException;
import pro.sky.telegrambot.service.TaskService;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {
    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private final TelegramBot telegramBot;
    private final TaskService taskService;

    public TelegramBotUpdatesListener(TelegramBot telegramBot, TaskService taskService) {
        this.telegramBot = telegramBot;
        this.taskService = taskService;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            if (update.message() == null || update.message().text() == null) return;

            Long chatId = update.message().chat().id();
            String text = update.message().text();

            try {
                switch (text) {
                    case "/start" -> sendWelcomeMessage(chatId);
                    case "/help" -> sendHelpMessage(chatId);
                    default -> {
                        taskService.parseAndSaveTask(chatId, text);
                        sendResponse(chatId, "✅ Задача успешно запланирована!");
                    }
                }
            } catch (InvalidTaskFormatException e) {
                sendResponse(chatId, "❌ Ошибка: " + e.getMessage());
            } catch (Exception e) {
                logger.error("Ошибка обработки сообщения", e);
                sendResponse(chatId, "⚠️ Произошла системная ошибка");
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void sendWelcomeMessage(Long chatId) {
        String message = "🌟 Привет! Я бот для напоминаний.\n\n"
                + "Формат задачи: <b>дд.мм.гггг чч:мм текст</b>\n"
                + "Пример: <code>31.12.2024 23:59 Поздравить с Новым Годом</code>";
        sendResponse(chatId, message);
    }

    private void sendHelpMessage(Long chatId) {
        sendResponse(chatId, "ℹ️ Доступные команды:\n/start - начать работу\n/help - справка");
    }

    private void sendResponse(Long chatId, String text) {
        SendMessage message = new SendMessage(chatId, text).parseMode(ParseMode.valueOf("HTML"));
        telegramBot.execute(message);
        logger.info("Отправлено сообщение в чат {}: {}", chatId, text);
    }
}