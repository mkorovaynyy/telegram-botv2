package pro.sky.telegrambot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pro.sky.telegrambot.model.NotificationStatus;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class NotificationTaskRepositoryTest {

    @Autowired
    private NotificationTaskRepository repository;

    private final Long testChatId = 123456L;
    private final String testMessage = "Тестовое уведомление";
    private NotificationTask testTask;

    @BeforeEach
    void setUp() {
        // Создаем задачу с временем на 1 час вперед
        LocalDateTime futureTime = LocalDateTime.now().plusHours(1);
        testTask = new NotificationTask(testChatId, testMessage, futureTime);
        testTask.setStatus(NotificationStatus.PENDING);
    }

    @Test
    void testSaveAndFindById() {
        // Сохранение задачи
        NotificationTask savedTask = repository.save(testTask);
        assertNotNull(savedTask.getId(), "ID не должен быть null после сохранения");

        // Поиск по ID
        NotificationTask foundTask = repository.findById(savedTask.getId()).orElse(null);
        assertNotNull(foundTask, "Задача должна быть найдена по ID");
        assertEquals(testMessage, foundTask.getMessage(), "Текст сообщения должен совпадать");
    }

    @Test
    void testFindPendingTasks() {
        // Сохраняем тестовую задачу
        repository.save(testTask);

        // Создаем просроченную задачу
        LocalDateTime pastTime = LocalDateTime.now().minusHours(1);
        NotificationTask expiredTask = new NotificationTask(testChatId, "Просроченная задача", pastTime);
        expiredTask.setStatus(NotificationStatus.PENDING);
        repository.save(expiredTask);

        // Создаем уже отправленную задачу
        LocalDateTime futureTime = LocalDateTime.now().plusHours(2);
        NotificationTask sentTask = new NotificationTask(testChatId, "Отправленная задача", futureTime);
        sentTask.setStatus(NotificationStatus.SENT);
        repository.save(sentTask);

        // Ищем задачи для отправки
        List<NotificationTask> pendingTasks = repository.findPendingTasks(
                NotificationStatus.PENDING,
                LocalDateTime.now()
        );

        // Проверяем результаты
        assertFalse(pendingTasks.isEmpty(), "Должны найтись pending задачи");
        assertEquals(1, pendingTasks.size(), "Должна быть найдена только одна актуальная задача");
        assertEquals(testMessage, pendingTasks.get(0).getMessage(), "Должна быть найдена тестовая задача");
    }

    @Test
    void testUpdateTaskStatus() {
        // Сохраняем задачу
        NotificationTask savedTask = repository.save(testTask);

        // Обновляем статус
        savedTask.setStatus(NotificationStatus.SENT);
        repository.save(savedTask);

        // Проверяем обновление
        NotificationTask updatedTask = repository.findById(savedTask.getId()).orElse(null);
        assertNotNull(updatedTask, "Задача должна быть найдена");
        assertEquals(NotificationStatus.SENT, updatedTask.getStatus(), "Статус должен быть обновлен");
    }
}