package com.backend.naukri.domain.application.service;

import com.backend.naukri.common.enums.ApplicationStatus;
import com.backend.naukri.common.enums.JobStatus;
import com.backend.naukri.domain.application.dto.*;
import com.backend.naukri.domain.application.entity.JobApplication;
import com.backend.naukri.domain.application.repository.JobApplicationRepository;
import com.backend.naukri.domain.candidate.entity.CandidateProfile;
import com.backend.naukri.domain.candidate.repository.CandidateProfileRepository;
import com.backend.naukri.domain.company.entity.CompanyProfile;
import com.backend.naukri.domain.company.repository.CompanyProfileRepository;
import com.backend.naukri.domain.job.entity.JobPosting;
import com.backend.naukri.domain.job.repository.JobPostingRepository;
import com.backend.naukri.domain.resume.entity.Resume;
import com.backend.naukri.domain.resume.repository.ResumeRepository;
import com.backend.naukri.exception.BadRequestException;
import com.backend.naukri.exception.ConflictException;
import com.backend.naukri.exception.ForbiddenException;
import com.backend.naukri.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Handles job application submission, status updates, and withdrawal.
 * Candidates apply and track; recruiters view and move through pipeline.
 */
@Service
@RequiredArgsConstructor
public class JobApplicationService {

    private final JobApplicationRepository applicationRepository;
    private final CandidateProfileRepository candidateProfileRepository;
    private final JobPostingRepository jobPostingRepository;
    private final ResumeRepository resumeRepository;
    private final CompanyProfileRepository companyProfileRepository;

    // ── Candidate Operations ──────────────────────────────────────────────────

    /**
     * Submits an application for a job.
     * Validates: job is ACTIVE, no duplicate application, resume exists.
     * Resume is automatically attached from the candidate's uploaded resume.
     */
    @Transactional
    public ApplicationDto apply(UUID userId, ApplyRequest request) {
        CandidateProfile profile = findCandidateProfile(userId);

        JobPosting job = jobPostingRepository.findById(request.getJobId())
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if (job.getStatus() != JobStatus.ACTIVE) {
            throw new BadRequestException("This job is no longer accepting applications.");
        }

        if (applicationRepository.existsByJobIdAndCandidateProfileId(job.getId(), profile.getId())) {
            throw new ConflictException("You have already applied to this job.");
        }

        // Resume is required to apply
        Resume resume = resumeRepository.findByCandidateProfileId(profile.getId())
                .orElseThrow(() -> new BadRequestException(
                        "Please upload a resume before applying."));

        JobApplication application = JobApplication.builder()
                .job(job)
                .candidateProfile(profile)
                .resume(resume)
                .coverLetter(request.getCoverLetter())
                .build();

        return ApplicationDto.from(applicationRepository.save(application));
    }

    /** Returns all applications submitted by the candidate, newest first. */
    @Transactional(readOnly = true)
    public List<ApplicationDto> getMyApplications(UUID userId) {
        CandidateProfile profile = findCandidateProfile(userId);
        return applicationRepository
                .findByCandidateProfileIdOrderByAppliedAtDesc(profile.getId())
                .stream().map(ApplicationDto::from).toList();
    }

    /**
     * Withdraws an application.
     * Only allowed when status is APPLIED — cannot withdraw once recruiter
     * has already viewed or shortlisted the application.
     */
    @Transactional
    public void withdraw(UUID userId, UUID applicationId) {
        CandidateProfile profile = findCandidateProfile(userId);

        JobApplication application = applicationRepository
                .findByIdAndCandidateProfileId(applicationId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (application.getStatus() != ApplicationStatus.APPLIED) {
            throw new BadRequestException(
                    "Cannot withdraw — application is already " + application.getStatus());
        }

        application.setStatus(ApplicationStatus.WITHDRAWN);
        application.setLastStatusChangedAt(LocalDateTime.now());
        applicationRepository.save(application);
    }

    // ── Recruiter Operations ──────────────────────────────────────────────────

    /**
     * Returns all applications for a specific job posting.
     * Recruiter must own the job.
     */
    @Transactional(readOnly = true)
    public List<ApplicationDetailDto> getApplicationsForJob(UUID userId, UUID jobId) {
        CompanyProfile company = findCompanyProfile(userId);

        JobPosting job = jobPostingRepository.findByIdAndCompanyId(jobId, company.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        return applicationRepository.findByJobIdOrderByAppliedAtDesc(job.getId())
                .stream().map(ApplicationDetailDto::from).toList();
    }

    /**
     * Returns full application detail for recruiter.
     * Auto-transitions status from APPLIED → VIEWED on first open.
     */
    @Transactional
    public ApplicationDetailDto getApplicationDetail(UUID userId, UUID applicationId) {
        CompanyProfile company = findCompanyProfile(userId);

        JobApplication application = applicationRepository
                .findByIdAndJobCompanyId(applicationId, company.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        // Auto-mark as VIEWED on first recruiter open
        if (application.getStatus() == ApplicationStatus.APPLIED) {
            application.setStatus(ApplicationStatus.VIEWED);
            application.setLastStatusChangedAt(LocalDateTime.now());
            applicationRepository.save(application);
        }

        return ApplicationDetailDto.from(application);
    }

    /**
     * Updates application status and optional recruiter note.
     * Validates allowed transitions to prevent invalid pipeline moves.
     */
    @Transactional
    public ApplicationDetailDto updateStatus(UUID userId, UUID applicationId,
                                             UpdateApplicationStatusRequest request) {
        CompanyProfile company = findCompanyProfile(userId);

        JobApplication application = applicationRepository
                .findByIdAndJobCompanyId(applicationId, company.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        validateStatusTransition(application.getStatus(), request.getStatus());

        application.setStatus(request.getStatus());
        application.setLastStatusChangedAt(LocalDateTime.now());

        if (request.getRecruiterNote() != null) {
            application.setRecruiterNote(request.getRecruiterNote());
        }

        return ApplicationDetailDto.from(applicationRepository.save(application));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private CandidateProfile findCandidateProfile(UUID userId) {
        return candidateProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate profile not found"));
    }

    private CompanyProfile findCompanyProfile(UUID userId) {
        return companyProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Company profile not found"));
    }

    /**
     * Enforces valid status transitions in the recruiter pipeline.
     * WITHDRAWN is terminal — recruiter cannot change it.
     * Candidates can only be moved forward or rejected.
     */
    private void validateStatusTransition(ApplicationStatus current, ApplicationStatus next) {
        if (current == ApplicationStatus.WITHDRAWN) {
            throw new ForbiddenException("Cannot update a withdrawn application.");
        }
        if (current == ApplicationStatus.HIRED || current == ApplicationStatus.REJECTED) {
            throw new BadRequestException("Application is already in a final state: " + current);
        }
        if (next == ApplicationStatus.APPLIED || next == ApplicationStatus.WITHDRAWN) {
            throw new BadRequestException("Cannot manually set status to " + next);
        }
    }
}
