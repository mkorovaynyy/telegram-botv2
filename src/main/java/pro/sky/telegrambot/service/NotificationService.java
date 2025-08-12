package pro.sky.telegrambot.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationStatus;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationTaskRepository repository;

    public NotificationService(NotificationTaskRepository repository) {
        this.repository = repository;
    }

    // Сохранение новой задачи
    public NotificationTask scheduleNotification(Long chatId, String message, LocalDateTime dateTime) {
        NotificationTask task = new NotificationTask(chatId, message, dateTime);
        return repository.save(task);
    }

    // Поиск задач для отправки (использует новый метод)
    public List<NotificationTask> getTasksForSending() {
        return repository.findPendingTasks(NotificationStatus.PENDING, LocalDateTime.now());
    }

    // Обновление статуса задачи
    public void markAsSent(NotificationTask task) {
        task.setStatus(NotificationStatus.SENT);
        repository.save(task);
    }

    // Отметка задачи как неудачной
    public void markAsFailed(NotificationTask task) {
        task.setStatus(NotificationStatus.FAILED);
        repository.save(task);
    }
}