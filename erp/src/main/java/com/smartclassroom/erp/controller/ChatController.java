package com.smartclassroom.erp.controller;

import com.smartclassroom.erp.dto.ChatMessageRequest;
import com.smartclassroom.erp.entity.ChatMessage;
import com.smartclassroom.erp.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/messages")
    public ResponseEntity<ChatMessage> sendMessage(@Valid @RequestBody ChatMessageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(chatService.sendMessage(request));
    }

    @GetMapping("/conversation/{firstUserId}/{secondUserId}")
    public ResponseEntity<List<ChatMessage>> getConversation(
            @PathVariable Long firstUserId,
            @PathVariable Long secondUserId) {
        return ResponseEntity.ok(chatService.getConversation(firstUserId, secondUserId));
    }

    @GetMapping("/inbox/{userId}")
    public ResponseEntity<List<ChatMessage>> getInbox(@PathVariable Long userId) {
        return ResponseEntity.ok(chatService.getInbox(userId));
    }

    @PutMapping("/messages/{id}/read")
    public ResponseEntity<ChatMessage> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(chatService.markAsRead(id));
    }
}
