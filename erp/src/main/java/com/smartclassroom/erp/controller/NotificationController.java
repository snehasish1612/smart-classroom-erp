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
    public ResponseEntity<List<Notification>> getAllNotifications() {
        return ResponseEntity.ok(
            notificationService.getAllNotifications());
    }

    // GET by role
    @GetMapping("/role/{role}")
    public ResponseEntity<List<Notification>> getNotificationsForRole(
            @PathVariable TargetRole role) {
        return ResponseEntity.ok(
            notificationService.getNotificationsForRole(role));
    }

    // GET unread by role
    @GetMapping("/unread/{role}")
    public ResponseEntity<List<Notification>> getUnreadNotifications(
            @PathVariable TargetRole role) {
        return ResponseEntity.ok(
            notificationService.getUnreadNotificationsForRole(role));
    }

    // GET unread count
    @GetMapping("/unread/count/{role}")
    public ResponseEntity<Long> getUnreadCount(
            @PathVariable TargetRole role) {
        return ResponseEntity.ok(
            notificationService.getUnreadCountForRole(role));
    }

    // POST - send notification by ADMIN
    @PostMapping("/send/admin")
    public ResponseEntity<Notification> sendNotificationByAdmin(
            @RequestParam Long sentById,
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam TargetRole targetRole) {
        Notification notification = notificationService
            .sendNotificationByAdmin(
                sentById, title, message, targetRole);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(notification);
    }

    // POST - send notification by AUTHORITY
    @PostMapping("/send/authority")
    public ResponseEntity<Notification> sendNotificationByAuthority(
            @RequestParam Long authorityId,
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam TargetRole targetRole) {
        Notification notification = notificationService
            .sendNotificationByAuthority(
                authorityId, title, message, targetRole);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(notification);
    }

    // PUT - mark as read
    @PutMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(
            @PathVariable Long id) {
        return ResponseEntity.ok(
            notificationService.markAsRead(id));
    }

    // PUT - mark all as read for role
    @PutMapping("/role/{role}/read-all")
    public ResponseEntity<List<Notification>> markAllAsRead(
            @PathVariable TargetRole role) {
        return ResponseEntity.ok(
            notificationService.markAllAsRead(role));
    }

    // DELETE notification
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNotification(
            @PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok("Notification deleted successfully!");
    }
}