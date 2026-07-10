package com.backend.naukri.domain.application.dto;

import com.backend.naukri.common.enums.ApplicationStatus;
import com.backend.naukri.domain.application.entity.JobApplication;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Recruiter-facing view of an application.
 * Includes full candidate details and recruiterNote.
 * Resume download is a separate endpoint.
 */
@Data
public class ApplicationDetailDto {

    private UUID id;
    private UUID jobId;
    private String jobTitle;

    // Candidate info
    private UUID candidateProfileId;
    private String candidateFirstName;
    private String candidateLastName;
    private String candidateHeadline;
    private String candidateCity;
    private String candidatePhone;
    private String coverLetter;

    // Resume info
    private UUID resumeId;
    private String resumeFileName;

    private ApplicationStatus status;
    private String recruiterNote;
    private LocalDateTime appliedAt;
    private LocalDateTime lastStatusChangedAt;

    public static ApplicationDetailDto from(JobApplication a) {
        ApplicationDetailDto dto = new ApplicationDetailDto();
        dto.setId(a.getId());
        dto.setJobId(a.getJob().getId());
        dto.setJobTitle(a.getJob().getTitle());

        dto.setCandidateProfileId(a.getCandidateProfile().getId());
        dto.setCandidateFirstName(a.getCandidateProfile().getFirstName());
        dto.setCandidateLastName(a.getCandidateProfile().getLastName());
        dto.setCandidateHeadline(a.getCandidateProfile().getHeadline());
        dto.setCandidateCity(a.getCandidateProfile().getCity());
        dto.setCandidatePhone(a.getCandidateProfile().getPhone());
        dto.setCoverLetter(a.getCoverLetter());

        if (a.getResume() != null) {
            dto.setResumeId(a.getResume().getId());
            dto.setResumeFileName(a.getResume().getOriginalFileName());
        }

        dto.setStatus(a.getStatus());
        dto.setRecruiterNote(a.getRecruiterNote());
        dto.setAppliedAt(a.getAppliedAt());
        dto.setLastStatusChangedAt(a.getLastStatusChangedAt());
        return dto;
    }
}
