package com.smartclassroom.erp.entity;

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

    // Which student this attendance belongs to
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // // Which faculty  this attendance belongs to
    @ManyToOne
    @JoinColumn(name = "faculty_id", nullable = false)
    private Faculty faculty;

    @NotBlank(message = "Subject is required!")
    @Column(nullable = false)
    private String subject;

    @NotNull(message = "Date is required!")
    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    public enum Status {       //Student ← Attendance → Faculty
        PRESENT,
        ABSENT
    }
}