package com.smartclassroom.erp.service;

import com.smartclassroom.erp.dto.ChatMessageRequest;
import com.smartclassroom.erp.entity.ChatMessage;
import com.smartclassroom.erp.entity.User;
import com.smartclassroom.erp.repository.ChatMessageRepository;
import com.smartclassroom.erp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    public ChatService(ChatMessageRepository chatMessageRepository, UserRepository userRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
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

        return chatMessageRepository.save(chatMessage);
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
        if (sender.getRole() == User.Role.STUDENT && receiver.getRole() != User.Role.FACULTY) {
            throw new RuntimeException("Students can chat with faculty only!");
        }

        if (sender.getRole() == User.Role.FACULTY && receiver.getRole() != User.Role.STUDENT) {
            throw new RuntimeException("Faculty can chat with students only!");
        }
    }
}
