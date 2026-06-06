package com.smartclassroom.erp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

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

    @NotBlank(message = "Password is required!")
    @Size(min = 6, message = "Password must be at least 6 characters!")
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public enum Role {
        ADMIN,
        FACULTY,
        STUDENT
    }
}