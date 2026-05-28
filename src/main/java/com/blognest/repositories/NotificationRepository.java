package com.blognest.repositories;

import com.blognest.models.Notification;
import com.blognest.models.User;
import com.blognest.models.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findByReceiver(User receiver);

    List<Notification> findByReceiverAndReadFalse(User receiver);

    List<Notification> findByReceiverAndType(User receiver, NotificationType type);
}