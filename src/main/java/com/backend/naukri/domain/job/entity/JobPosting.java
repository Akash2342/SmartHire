package com.backend.naukri.domain.job.entity;

import com.backend.naukri.common.enums.*;
import com.backend.naukri.domain.company.entity.CompanyProfile;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a job posting created by a recruiter.
 * Only ACTIVE postings appear in candidate search results.
 * Recruiter's company must be VERIFIED before posting.
 *
 * Status lifecycle: DRAFT → ACTIVE ⇄ PAUSED → CLOSED / EXPIRED
 */
@Entity
@Table(name = "job_postings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPosting {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Company that owns this job posting. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private CompanyProfile company;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 5000)
    private String description;

    @Column(length = 3000)
    private String requirements;

    private String category;
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmploymentType employmentType;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private WorkMode workMode = WorkMode.ON_SITE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExperienceLevel experienceLevel;

    private Integer minYearsExperience;
    private Integer salaryMin;
    private Integer salaryMax;

    @Builder.Default
    private String salaryCurrency = "INR";

    /** When false, salary range is hidden from candidates in search results. */
    @Builder.Default
    private boolean isSalaryVisible = true;

    @Builder.Default
    private Integer numberOfOpenings = 1;

    private LocalDate applicationDeadline;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private JobStatus status = JobStatus.DRAFT;

    /** Total number of times this job detail page was viewed. */
    @Builder.Default
    private Integer viewCount = 0;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /** Set when status changes to ACTIVE for the first time. */
    private LocalDateTime postedAt;

    /** Set when status changes to CLOSED. */
    private LocalDateTime closedAt;
}
