package com.cognizant.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.cognizant.entities.Notification;
import com.cognizant.repositries.NotificationRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api")
@CrossOrigin(origins = "*")
@Tag(name = "Notifications", description = "In-app notification API")
public class NotificationController {

    private final NotificationRepository notifRepo;
    private final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    public NotificationController(NotificationRepository notifRepo) {
        this.notifRepo = notifRepo;
    }

    /**
     * GET /api/notifications
     * Returns only UNREAD notifications for the logged-in user.
     * Once read, they no longer show in the panel — clean and intentional.
     */
    @Operation(description = "Get unread notifications for the logged-in user")
    @GetMapping("/notifications")
    public ResponseEntity<List<Notification>> getMyNotifications(Authentication auth) {
        String userName = auth.getName();
        List<Notification> unread =
            notifRepo.findByRecipientAndIsReadFalseOrderByCreatedAtDesc(userName);
        return ResponseEntity.ok(unread);
    }

    /**
     * GET /api/notifications/all
     * Returns ALL notifications (read + unread), last 50, for a history view.
     */
    @Operation(description = "Get all notifications (read + unread) for history view")
    @GetMapping("/notifications/all")
    public ResponseEntity<List<Notification>> getAllMyNotifications(Authentication auth) {
        return ResponseEntity.ok(
            notifRepo.findByRecipientOrderByCreatedAtDesc(auth.getName())
        );
    }

    /**
     * GET /api/notifications/unread/count
     * Badge count only — lightweight.
     */
    @Operation(description = "Get unread notification count")
    @GetMapping("/notifications/unread/count")
    public ResponseEntity<?> getUnreadCount(Authentication auth) {
        long count = notifRepo.countByRecipientAndIsReadFalse(auth.getName());
        return ResponseEntity.ok("{\"count\": " + count + "}");
    }

    /**
     * PUT /api/notifications/{id}/read
     * Mark one as read — it disappears from the unread feed on next poll.
     */
    @Operation(description = "Mark a single notification as read")
    @PutMapping("/notifications/{id}/read")
    public ResponseEntity<?> markRead(@PathVariable Integer id, Authentication auth) {
        return notifRepo.findById(id).map(n -> {
            if (!n.getRecipient().equals(auth.getName())) {
                return ResponseEntity.status(403).<String>body("Not your notification");
            }
            n.setRead(true);
            notifRepo.save(n);
            return ResponseEntity.ok("{\"message\": \"Marked as read\"}");
        }).orElse(ResponseEntity.<String>notFound().build());
    }

    /**
     * PUT /api/notifications/read-all
     * Mark all unread as read at once.
     */
    @Operation(description = "Mark all my notifications as read")
    @PutMapping("/notifications/read-all")
    public ResponseEntity<?> markAllRead(Authentication auth) {
        String userName = auth.getName();
        List<Notification> unread =
            notifRepo.findByRecipientAndIsReadFalseOrderByCreatedAtDesc(userName);
        unread.forEach(n -> n.setRead(true));
        notifRepo.saveAll(unread);
        return ResponseEntity.ok("{\"message\": \"All " + unread.size() + " notifications marked as read\"}");
    }

    /**
     * DELETE /api/notifications/old
     * Housekeeping: delete notifications older than 30 days.
     * Call this on a schedule or manually.
     */
    @Operation(description = "Delete notifications older than 30 days")
    @DeleteMapping("/notifications/old")
    public ResponseEntity<?> deleteOld(Authentication auth) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
        List<Notification> old = notifRepo.findByRecipientOrderByCreatedAtDesc(auth.getName())
            .stream()
            .filter(n -> n.getCreatedAt() != null && n.getCreatedAt().isBefore(cutoff))
            .toList();
        notifRepo.deleteAll(old);
        return ResponseEntity.ok("{\"deleted\": " + old.size() + "}");
    }
}
