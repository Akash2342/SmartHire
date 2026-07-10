package com.backend.naukri.domain.application.dto;

import com.backend.naukri.common.enums.ApplicationStatus;
import com.backend.naukri.domain.application.entity.JobApplication;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Candidate-facing view of an application.
 * Does NOT include recruiterNote — that is internal only.
 */
@Data
public class ApplicationDto {

    private UUID id;
    private UUID jobId;
    private String jobTitle;
    private String companyName;
    private String location;
    private ApplicationStatus status;
    private String coverLetter;
    private LocalDateTime appliedAt;
    private LocalDateTime lastStatusChangedAt;

    public static ApplicationDto from(JobApplication a) {
        ApplicationDto dto = new ApplicationDto();
        dto.setId(a.getId());
        dto.setJobId(a.getJob().getId());
        dto.setJobTitle(a.getJob().getTitle());
        dto.setCompanyName(a.getJob().getCompany().getCompanyName());
        dto.setLocation(a.getJob().getLocation());
        dto.setStatus(a.getStatus());
        dto.setCoverLetter(a.getCoverLetter());
        dto.setAppliedAt(a.getAppliedAt());
        dto.setLastStatusChangedAt(a.getLastStatusChangedAt());
        return dto;
    }
}
