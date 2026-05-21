package com.grupo1.backGrupo1.repository;

import com.grupo1.backGrupo1.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByDeletedFalseAndTipoOrderByCreatedAtDesc(Notification.Type tipo);

    List<Notification> findByDeletedFalseAndEventIdOrderByCreatedAtDesc(Long eventId);

    List<Notification> findByDeletedFalseOrderByCreatedAtDesc();
}
