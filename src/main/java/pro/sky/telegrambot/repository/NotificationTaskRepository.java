package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.model.NotificationStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationTaskRepository extends JpaRepository<NotificationTask, Long> {

    @Query("SELECT t FROM NotificationTask t " +
            "WHERE t.status = :status AND t.notificationDateTime <= :now")
    List<NotificationTask> findPendingTasks(
            @Param("status") NotificationStatus status,
            @Param("now") LocalDateTime now
    );
}