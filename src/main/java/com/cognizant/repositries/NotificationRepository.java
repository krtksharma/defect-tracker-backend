package com.cognizant.repositries;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.cognizant.entities.Notification;

@Repository
public interface NotificationRepository extends CrudRepository<Notification, Integer> {
    // Get all notifications for a user, newest first
    List<Notification> findByRecipientOrderByCreatedAtDesc(String recipient);
    // Get only unread
    List<Notification> findByRecipientAndIsReadFalseOrderByCreatedAtDesc(String recipient);
    // Count unread
    long countByRecipientAndIsReadFalse(String recipient);
}
