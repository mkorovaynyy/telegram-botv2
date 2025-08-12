package pro.sky.telegrambot.model;

public enum NotificationStatus {
    PENDING,   // Задача ожидает отправки
    SENT,      // Уведомление успешно отправлено
    FAILED     // Не удалось отправить уведомление
}