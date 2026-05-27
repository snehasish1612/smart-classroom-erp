package com.smartclassroom.erp.controller;

import com.smartclassroom.erp.entity.Notification;
import com.smartclassroom.erp.entity.Notification.TargetRole;
import com.smartclassroom.erp.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // GET all notifications
    @GetMapping
    public ResponseEntity<List<Notification>> getAll() {
        return ResponseEntity.ok(notificationService.getAll());
    }

    // GET by role
    // URL: /api/notifications/role/STUDENT
    @GetMapping("/role/{role}")
    public ResponseEntity<List<Notification>> getByRole(
            @PathVariable TargetRole role) {
        return ResponseEntity.ok(notificationService.getByRole(role));
    }

    // GET unread by role
    // URL: /api/notifications/unread/STUDENT
    @GetMapping("/unread/{role}")
    public ResponseEntity<List<Notification>> getUnread(
            @PathVariable TargetRole role) {
        return ResponseEntity.ok(notificationService.getUnread(role));
    }

    // GET unread count
    // URL: /api/notifications/unread/count/STUDENT
    @GetMapping("/unread/count/{role}")
    public ResponseEntity<Long> getUnreadCount(
            @PathVariable TargetRole role) {
        return ResponseEntity.ok(notificationService.getUnreadCount(role));
    }

    // POST → create notification (ADMIN only)
    // URL: /api/notifications?userId=1&title=...&message=...&role=STUDENT
    @PostMapping
    public ResponseEntity<Notification> create(
            @RequestParam Long userId,
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam TargetRole role) {
        Notification saved = notificationService.create(
            userId, title, message, role);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT → mark as read
    // URL: /api/notifications/1/read
    @PutMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }

    // DELETE notification
    // URL: /api/notifications/1
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        notificationService.delete(id);
        return ResponseEntity.ok("Notification deleted successfully!");
    }
}