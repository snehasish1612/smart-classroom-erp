package com.smartclassroom.erp.repository;

import com.smartclassroom.erp.entity.Notification;
import com.smartclassroom.erp.entity.Notification.TargetRole;
import com.smartclassroom.erp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // ROLE BASED (includes ALL users)
    @Query("""
        SELECT n FROM Notification n
        WHERE n.targetRole = ?1 OR n.targetRole = 'ALL'
        ORDER BY n.createdAt DESC
    """)
    List<Notification> findByRole(TargetRole role);

    // UNREAD NOTIFICATIONS
    @Query("""
        SELECT n FROM Notification n
        WHERE (n.targetRole = ?1 OR n.targetRole = 'ALL')
        AND n.isRead = false
    """)
    List<Notification> findUnreadByRole(TargetRole role);

    // COUNT UNREAD
    @Query("""
        SELECT COUNT(n) FROM Notification n
        WHERE (n.targetRole = ?1 OR n.targetRole = 'ALL')
        AND n.isRead = false
    """)
    Long countUnreadByRole(TargetRole role);

    // SENT BY USER
    List<Notification> findBySentBy(User sentBy);

    // SORTED
    List<Notification> findAllByOrderByCreatedAtDesc();
}