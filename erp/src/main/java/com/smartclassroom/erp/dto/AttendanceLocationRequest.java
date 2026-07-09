package com.smartclassroom.erp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AttendanceLocationRequest {

    @NotNull
    private Long studentId;

    @NotNull
    private Long facultyId;

    @NotNull
    private Long sectionId;

    @NotBlank
    private String subject;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;
}
