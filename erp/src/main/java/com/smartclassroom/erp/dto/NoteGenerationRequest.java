package com.smartclassroom.erp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NoteGenerationRequest {

    @NotBlank
    private String topic;

    @NotBlank
    private String subject;

    @NotNull
    private Long createdByUserId;
}
