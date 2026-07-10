package com.backend.naukri.domain.application.dto;

import com.backend.naukri.common.enums.ApplicationStatus;
import lombok.Data;

@Data
public class UpdateApplicationStatusRequest {

    private ApplicationStatus status;

    /** Optional internal note from recruiter. Not visible to candidate. */
    private String recruiterNote;
}
