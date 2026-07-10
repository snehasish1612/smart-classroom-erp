package com.smartclassroom.erp.service;

import com.smartclassroom.erp.dto.ChatMessageRequest;
import com.smartclassroom.erp.entity.ChatMessage;
import com.smartclassroom.erp.entity.User;
import com.smartclassroom.erp.repository.ChatMessageRepository;
import com.smartclassroom.erp.repository.NotificationRepository;
import com.smartclassroom.erp.repository.UserRepository;
import com.smartclassroom.erp.entity.Notification;
import com.smartclassroom.erp.entity.Notification.TargetRole;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

@Service
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatService(ChatMessageRepository chatMessageRepository, UserRepository userRepository, NotificationRepository notificationRepository, SimpMessagingTemplate messagingTemplate) {
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public ChatMessage sendMessage(ChatMessageRequest request) {
        User sender = userRepository.findById(request.getSenderId())
            .orElseThrow(() -> new RuntimeException("Sender not found!"));
        User receiver = userRepository.findById(request.getReceiverId())
            .orElseThrow(() -> new RuntimeException("Receiver not found!"));
        validateChatParticipants(sender, receiver);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSender(sender);
        chatMessage.setReceiver(receiver);
        chatMessage.setMessage(request.getMessage());
        chatMessage.setIsRead(false);

        ChatMessage saved = chatMessageRepository.save(chatMessage);

        // Create a notification for the receiver so it shows in the notification panel
        try {
            Notification notification = new Notification();
            notification.setTitle("New message from " + sender.getName());
            String body = request.getMessage() == null ? "" : request.getMessage();
            notification.setMessage(body.length() > 100 ? body.substring(0, 100) + "..." : body);
            notification.setSentBy(sender);
            notification.setIsRead(false);
            // target role is based on receiver role
            notification.setTargetRole(receiver.getRole() == User.Role.FACULTY ? TargetRole.FACULTY : TargetRole.STUDENT);
            Notification savedNotif = notificationRepository.save(notification);

            // Push real-time events: message to receiver's private queue and notification to receiver
            try {
                messagingTemplate.convertAndSendToUser(receiver.getEmail(), "/queue/messages", saved);
                messagingTemplate.convertAndSendToUser(receiver.getEmail(), "/queue/notifications", savedNotif);
            } catch (Exception ex) {
                // ignore messaging failures
            }
        } catch (Exception ex) {
            // don't block message send if notification fails
        }

        // Also send message to receiver via websocket even if notification creation failed
        try {
            messagingTemplate.convertAndSendToUser(receiver.getEmail(), "/queue/messages", saved);
        } catch (Exception ex) {
            // ignore
        }

        return saved;
    }

    public List<ChatMessage> getConversation(Long firstUserId, Long secondUserId) {
        User firstUser = userRepository.findById(firstUserId)
            .orElseThrow(() -> new RuntimeException("First user not found!"));
        User secondUser = userRepository.findById(secondUserId)
            .orElseThrow(() -> new RuntimeException("Second user not found!"));
        validateChatParticipants(firstUser, secondUser);
        return chatMessageRepository.findConversation(firstUser, secondUser);
    }

    public List<ChatMessage> getInbox(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found!"));
        return chatMessageRepository.findByReceiverOrderBySentAtDesc(user);
    }

    public ChatMessage markAsRead(Long id) {
        ChatMessage message = chatMessageRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Message not found!"));
        message.setIsRead(true);
        return chatMessageRepository.save(message);
    }

    private void validateChatParticipants(User sender, User receiver) {
        // Allow ADMIN to communicate with anyone
        if (sender.getRole() == User.Role.ADMIN || receiver.getRole() == User.Role.ADMIN) {
            return;
        }

        if (sender.getRole() == User.Role.STUDENT && receiver.getRole() != User.Role.FACULTY) {
            throw new RuntimeException("Students can chat with faculty only!");
        }

        if (sender.getRole() == User.Role.FACULTY && receiver.getRole() != User.Role.STUDENT) {
            throw new RuntimeException("Faculty can chat with students only!");
        }
    }
}
