package com.smartclassroom.erp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AssignmentRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String subject;

    private String description;

    @NotNull
    private LocalDate dueDate;

    @NotNull
    private Long facultyId;

    private Long sectionId;
}
