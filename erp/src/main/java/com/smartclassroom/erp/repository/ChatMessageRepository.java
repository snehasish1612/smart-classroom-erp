package com.smartclassroom.erp.repository;

import com.smartclassroom.erp.entity.ChatMessage;
import com.smartclassroom.erp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    @Query("""
        SELECT m FROM ChatMessage m
        WHERE (m.sender = ?1 AND m.receiver = ?2)
           OR (m.sender = ?2 AND m.receiver = ?1)
        ORDER BY m.sentAt ASC
    """)
    List<ChatMessage> findConversation(User firstUser, User secondUser);

    List<ChatMessage> findByReceiverOrderBySentAtDesc(User receiver);
}
