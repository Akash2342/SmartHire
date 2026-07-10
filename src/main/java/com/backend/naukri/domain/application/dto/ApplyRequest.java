package com.backend.naukri.domain.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ApplyRequest {

    @NotNull(message = "Job ID is required")
    private UUID jobId;

    /** Optional cover letter — max 2000 characters. */
    private String coverLetter;
}
