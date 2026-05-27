package com.smartclassroom.erp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "devices")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Device name is required!")
    @Pattern(regexp = "Lights|Projector|AC|Fan|SmartBoard",
             message = "Device must be Lights, Projector, AC, Fan or SmartBoard!")
    @Column(nullable = false)
    private String deviceName;

    @NotBlank(message = "Status is required!")
    @Pattern(regexp = "ON|OFF", message = "Status must be ON or OFF!")
    @Column(nullable = false)
    private String status;

    @NotNull(message = "Classroom ID is required!")
    @Column(nullable = false)
    private Long classroomId;
}