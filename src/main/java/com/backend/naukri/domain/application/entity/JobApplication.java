package com.backend.naukri.domain.application.entity;

import com.backend.naukri.common.enums.ApplicationStatus;
import com.backend.naukri.domain.candidate.entity.CandidateProfile;
import com.backend.naukri.domain.job.entity.JobPosting;
import com.backend.naukri.domain.resume.entity.Resume;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a candidate's application to a specific job posting.
 * Unique constraint on (job + candidate) prevents duplicate applications.
 * Status is updated by recruiter as candidate moves through the pipeline.
 */
@Entity
@Table(name = "job_applications",
        uniqueConstraints = @UniqueConstraint(columnNames = {"job_id", "candidate_profile_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private JobPosting job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_profile_id", nullable = false)
    private CandidateProfile candidateProfile;

    /** Resume snapshot at the time of application. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id")
    private Resume resume;

    @Column(length = 2000)
    private String coverLetter;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ApplicationStatus status = ApplicationStatus.APPLIED;

    /** Internal note by recruiter — never visible to candidate. */
    @Column(length = 1000)
    private String recruiterNote;

    @CreationTimestamp
    private LocalDateTime appliedAt;

    @Builder.Default
    private LocalDateTime lastStatusChangedAt = LocalDateTime.now();
}
