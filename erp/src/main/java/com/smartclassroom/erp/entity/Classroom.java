package com.smartclassroom.erp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "classrooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Classroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String roomNumber;

    private String building;

    private Integer capacity;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        AVAILABLE,
        OCCUPIED
    }
}