package pro.sky.telegrambot.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambot.exception.InvalidTaskFormatException;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TaskService {
    private static final Pattern TASK_PATTERN = Pattern.compile(
            "(\\d{2}\\.\\d{2}\\.\\d{4} \\d{1,2}:\\d{2})\\s+(.+)"
    );
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private final NotificationTaskRepository repository;

    public TaskService(NotificationTaskRepository repository) {
        this.repository = repository;
    }

    public NotificationTask parseAndSaveTask(Long chatId, String text) {
        Matcher matcher = TASK_PATTERN.matcher(text);
        if (!matcher.find()) {
            throw new InvalidTaskFormatException("Неверный формат! Пример: 01.01.2025 12:00 Текст задачи");
        }

        try {
            LocalDateTime dateTime = LocalDateTime.parse(matcher.group(1), FORMATTER);
            validateDateTime(dateTime);

            NotificationTask task = new NotificationTask(
                    chatId,
                    matcher.group(2).trim(),
                    dateTime
            );
            return repository.save(task);
        } catch (DateTimeParseException e) {
            throw new InvalidTaskFormatException("Ошибка формата даты. Используйте ДД.ММ.ГГГГ ЧЧ:ММ");
        }
    }

    private void validateDateTime(LocalDateTime dateTime) {
        if (dateTime.isBefore(LocalDateTime.now().plusMinutes(1))) {
            throw new InvalidTaskFormatException("Дата должна быть минимум на 1 минуту позже текущего времени");
        }
    }
}
