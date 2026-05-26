package com.smartclassroom.erp.service;

import com.smartclassroom.erp.config.ResourceNotFoundException;
import com.smartclassroom.erp.entity.Notification;
import com.smartclassroom.erp.entity.Notification.TargetRole;
import com.smartclassroom.erp.entity.User;
import com.smartclassroom.erp.repository.NotificationRepository;
import com.smartclassroom.erp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    // GET ALL
    public List<Notification> getAll() {
        return notificationRepository.findAllByOrderByCreatedAtDesc();
    }

    // GET BY ROLE
    public List<Notification> getByRole(TargetRole role) {
        return notificationRepository.findByRole(role);
    }

    // GET UNREAD
    public List<Notification> getUnread(TargetRole role) {
        return notificationRepository.findUnreadByRole(role);
    }

    // COUNT UNREAD
    public Long getUnreadCount(TargetRole role) {
        return notificationRepository.countUnreadByRole(role);
    }

    // CREATE (ONLY ADMIN CAN SEND)
    public Notification create(Long userId, String title, String message, TargetRole role) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Only ADMIN can send notifications");
        }

        if (title == null || title.trim().isEmpty()) {
            throw new RuntimeException("Title cannot be empty");
        }

        if (message == null || message.trim().isEmpty()) {
            throw new RuntimeException("Message cannot be empty");
        }

        Notification n = new Notification();
        n.setTitle(title);
        n.setMessage(message);
        n.setSentBy(user);
        n.setTargetRole(role);
        n.setIsRead(false);

        return notificationRepository.save(n);
    }

    // MARK AS READ
    public Notification markAsRead(Long id) {

        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if (Boolean.TRUE.equals(n.getIsRead())) {
            throw new RuntimeException("Already marked as read");
        }

        n.setIsRead(true);
        return notificationRepository.save(n);
    }

    // DELETE
    public void delete(Long id) {

        if (!notificationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Notification not found");
        }

        notificationRepository.deleteById(id);
    }
}