package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationStatus;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationSenderService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationSenderService.class);
    private final TelegramBot telegramBot;
    private final NotificationTaskRepository repository;

    public NotificationSenderService(TelegramBot telegramBot, NotificationTaskRepository repository) {
        this.telegramBot = telegramBot;
        this.repository = repository;
    }

    @Scheduled(fixedRate = 30_000)
    public void sendScheduledNotifications() {
        LocalDateTime now = LocalDateTime.now();
        List<NotificationTask> tasks = repository.findPendingTasks(NotificationStatus.PENDING, now);

        if (tasks.isEmpty()) {
            logger.debug("Нет задач для отправки");
            return;
        }

        logger.info("Найдено задач: {}", tasks.size());
        tasks.forEach(this::sendNotification);
    }

    private void sendNotification(NotificationTask task) {
        try {
            String text = "⏰ Напоминание: " + task.getMessage();
            SendMessage message = new SendMessage(task.getChatId(), text);
            telegramBot.execute(message);

            task.setStatus(NotificationStatus.SENT);
            repository.save(task);
            logger.info("Уведомление отправлено: {}", task.getId());
        } catch (Exception e) {
            task.setStatus(NotificationStatus.FAILED);
            repository.save(task);
            logger.error("Ошибка отправки уведомления {}", task.getId(), e);
        }
    }
}
