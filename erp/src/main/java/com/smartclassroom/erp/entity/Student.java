package com.smartclassroom.erp.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required!")
    @Column(nullable = false)
    private String name;

    @Email(message = "Invalid email format!")
    @NotBlank(message = "Email is required!")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Roll number is required!")
    @Column(unique = true, nullable = false)
    private String rollNumber;

    @Column(nullable = false)
    private String department;

    @Column(nullable = false)
    private Integer semester;

    @Pattern(
        regexp = "^[0-9]{10}$",
        message = "Phone number must contain exactly 10 digits!"
    )
    private String phone;

    @ManyToOne
    @JoinColumn(name = "section_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @JsonBackReference
    private Section section;

    @PrePersist
    @PreUpdate
    private void applyDefaults() {
        if (department == null || department.isBlank()) {
            department = section != null && section.getStream() != null && section.getStream().getName() != null
                ? section.getStream().getName()
                : "CSE";
        }

        if (semester == null) {
            semester = section != null && section.getSemester() != null ? section.getSemester() : 1;
        }
    }
}
