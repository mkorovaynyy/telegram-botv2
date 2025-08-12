package pro.sky.telegrambot.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification_task")
public class NotificationTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "notification_datetime", nullable = false)
    private LocalDateTime notificationDateTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status = NotificationStatus.PENDING;

    // Конструкторы, геттеры и сеттеры

    public NotificationTask() {
    }

    public NotificationTask(Long chatId, String message, LocalDateTime notificationDateTime) {
        this.chatId = chatId;
        this.message = message;
        this.notificationDateTime = notificationDateTime;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getNotificationDateTime() {
        return notificationDateTime;
    }

    public void setNotificationDateTime(LocalDateTime notificationDateTime) {
        this.notificationDateTime = notificationDateTime;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "NotificationTask{" +
                "id=" + id +
                ", chatId=" + chatId +
                ", message='" + message + '\'' +
                ", notificationDateTime=" + notificationDateTime +
                ", status=" + status +
                '}';
    }
}