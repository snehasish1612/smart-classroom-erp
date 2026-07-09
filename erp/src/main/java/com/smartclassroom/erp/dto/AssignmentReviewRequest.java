package com.smartclassroom.erp.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignmentReviewRequest {

    @NotNull
    @Min(0)
    @Max(100)
    private Integer marks;

    private String feedback;
}
