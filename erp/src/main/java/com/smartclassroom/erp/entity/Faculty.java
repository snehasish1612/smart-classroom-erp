package com.smartclassroom.erp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "faculty")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Faculty {

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

    @NotBlank(message = "Department is required!")
    @Column(nullable = false)
    private String department;

    @Pattern(
        regexp = "^[0-9]{10}$",
        message = "Phone must be 10 digits!"
    )
    private String phone;

    @NotBlank(message = "Designation is required!")
    @Column(nullable = false)
    private String designation;

    private String subjectsTaught;
}