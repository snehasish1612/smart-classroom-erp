package com.smartclassroom.erp.service;

import com.smartclassroom.erp.entity.Authority;
import com.smartclassroom.erp.entity.Notification;
import com.smartclassroom.erp.entity.Notification.TargetRole;
import com.smartclassroom.erp.entity.User;
import com.smartclassroom.erp.repository.AuthorityRepository;
import com.smartclassroom.erp.repository.NotificationRepository;
import com.smartclassroom.erp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    // Get all notifications
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAllByOrderByCreatedAtDesc();
    }

    // Get notification by id
    public Optional<Notification> getNotificationById(Long id) {
        return notificationRepository.findById(id);
    }

    // Get notifications for a specific role
    public List<Notification> getNotificationsForRole(TargetRole role) {
        return notificationRepository.findByRole(role);
    }

    // Get unread notifications for a role
    public List<Notification> getUnreadNotificationsForRole(
            TargetRole role) {
        return notificationRepository.findUnreadByRole(role);
    }

    // Get unread count for a role
    public Long getUnreadCountForRole(TargetRole role) {
        return notificationRepository.countUnreadByRole(role);
    }

    // Send notification by ADMIN
    public Notification sendNotificationByAdmin(
            Long sentById, String title,
            String message, TargetRole targetRole) {

        // Step 1: Find user
        User sentBy = userRepository.findById(sentById)
            .orElseThrow(() -> 
                new RuntimeException("User not found!"));

        // Step 2: Check role
        if (sentBy.getRole() != User.Role.ADMIN) {
            throw new RuntimeException(
                "Only ADMIN can send notifications!");
        }

        // Step 3: Validate
        if (title == null || title.trim().isEmpty()) {
            throw new RuntimeException("Title cannot be empty!");
        }
        if (message == null || message.trim().isEmpty()) {
            throw new RuntimeException("Message cannot be empty!");
        }

        // Step 4: Create and save
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setSentBy(sentBy);
        notification.setTargetRole(targetRole);
        notification.setIsRead(false);

        return notificationRepository.save(notification);
    }

    // Send notification by AUTHORITY
    public Notification sendNotificationByAuthority(
            Long authorityId, String title,
            String message, TargetRole targetRole) {

        // Step 1: Find authority
        Authority authority = authorityRepository.findById(authorityId)
            .orElseThrow(() ->
                new RuntimeException("Authority not found!"));

        // Step 2: Validate
        if (title == null || title.trim().isEmpty()) {
            throw new RuntimeException("Title cannot be empty!");
        }
        if (message == null || message.trim().isEmpty()) {
            throw new RuntimeException("Message cannot be empty!");
        }

        // Step 3: Find admin user to link notification
        User adminUser = userRepository.findAll()
            .stream()
            .filter(u -> u.getRole() == User.Role.ADMIN)
            .findFirst()
            .orElseThrow(() ->
                new RuntimeException(
                    "No admin user found!"));

        // Step 4: Create and save
        Notification notification = new Notification();
        notification.setTitle("[AUTHORITY] " + title);
        notification.setMessage(message);
        notification.setSentBy(adminUser);
        notification.setTargetRole(targetRole);
        notification.setIsRead(false);

        return notificationRepository.save(notification);
    }

    // Mark notification as read
    public Notification markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
            .orElseThrow(() ->
                new RuntimeException("Notification not found!"));

        if (notification.getIsRead()) {
            throw new RuntimeException(
                "Notification already marked as read!");
        }

        notification.setIsRead(true);
        return notificationRepository.save(notification);
    }

    // Mark ALL notifications as read for a role
    public List<Notification> markAllAsRead(TargetRole role) {
        List<Notification> notifications = notificationRepository
            .findByRole(role);

        notifications.forEach(n -> n.setIsRead(true));
        return notificationRepository.saveAll(notifications);
    }

    // Delete notification
    public void deleteNotification(Long id) {
        if (!notificationRepository.existsById(id)) {
            throw new RuntimeException("Notification not found!");
        }
        notificationRepository.deleteById(id);
    }
}