package com.smartclassroom.erp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String message;

    @ManyToOne
    @JoinColumn(name = "sent_by", nullable = false)
    private User sentBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TargetRole targetRole;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private Boolean isRead;

    public enum TargetRole {
        ALL,
        STUDENT,
        FACULTY
    }

   // Set createdAt automatically before saving
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
    }
