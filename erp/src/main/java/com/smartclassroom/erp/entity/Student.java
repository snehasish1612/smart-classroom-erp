package com.smartclassroom.erp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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

    @NotBlank(message = "Department is required!")
    @Column(nullable = false)
    private String department;

    @NotNull(message = "Semester is required!")
    @Min(value = 1, message = "Semester must be at least 1!")
    @Max(value = 8, message = "Semester cannot exceed 8!")
    @Column(nullable = false)
    private Integer semester;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits!")
    private String phone;
}