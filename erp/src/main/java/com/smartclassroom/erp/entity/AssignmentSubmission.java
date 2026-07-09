package com.smartclassroom.erp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "assignment_submissions",
    uniqueConstraints = @UniqueConstraint(columnNames = {"assignment_id", "student_id"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @NotBlank
    @Column(nullable = false, length = 2000)
    private String content;

    private String attachmentUrl;

    @Column(nullable = false)
    private LocalDateTime submittedAt;

    private Integer marks;

    @Column(length = 1000)
    private String feedback;

    private LocalDateTime reviewedAt;

    @PrePersist
    public void prePersist() {
        this.submittedAt = LocalDateTime.now();
    }
}
