package com.smartclassroom.erp.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "attendance")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Which student
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // Which faculty took attendance
    @ManyToOne
    @JoinColumn(name = "faculty_id", nullable = false)
    private Faculty faculty;

    // Which section
    @ManyToOne
    @JoinColumn(name = "section_id", nullable = false)
    @JsonBackReference
    private Section section;

    @NotBlank(message = "Subject is required!")
    @Column(nullable = false)
    private String subject;

    @NotNull(message = "Date is required!")
    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    public enum Status {
        PRESENT,
        ABSENT
    }
}