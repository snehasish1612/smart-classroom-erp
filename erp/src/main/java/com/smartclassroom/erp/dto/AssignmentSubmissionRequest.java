package com.smartclassroom.erp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignmentSubmissionRequest {
    @NotNull
    private Long studentId;

    @NotBlank
    private String content;

    private String attachmentUrl;
}
