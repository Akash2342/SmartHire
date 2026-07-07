package com.backend.naukri.domain.job.service;

import com.backend.naukri.common.enums.*;
import com.backend.naukri.common.dto.PagedResponse;
import com.backend.naukri.domain.company.entity.CompanyProfile;
import com.backend.naukri.domain.company.repository.CompanyProfileRepository;
import com.backend.naukri.domain.job.dto.CreateJobRequest;
import com.backend.naukri.domain.job.dto.JobPostingDto;
import com.backend.naukri.domain.job.dto.UpdateJobRequest;
import com.backend.naukri.domain.job.entity.JobPosting;
import com.backend.naukri.domain.job.repository.JobPostingRepository;
import com.backend.naukri.exception.BadRequestException;
import com.backend.naukri.exception.ForbiddenException;
import com.backend.naukri.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Handles all job posting operations for recruiters and public job search.
 * Only recruiters with VERIFIED companies can create/manage postings.
 */
@Service
@RequiredArgsConstructor
public class JobPostingService {

    private final JobPostingRepository jobPostingRepository;
    private final CompanyProfileRepository companyProfileRepository;

    // ── Recruiter Operations ──────────────────────────────────────────────────

    /**
     * Creates a new job posting as DRAFT.
     * Recruiter's company must be VERIFIED.
     */
    @Transactional
    public JobPostingDto create(UUID userId, CreateJobRequest request) {
        CompanyProfile company = getVerifiedCompany(userId);

        JobPosting job = JobPosting.builder()
                .company(company)
                .title(request.getTitle())
                .description(request.getDescription())
                .requirements(request.getRequirements())
                .category(request.getCategory())
                .location(request.getLocation())
                .employmentType(request.getEmploymentType())
                .workMode(request.getWorkMode() != null ? request.getWorkMode() : WorkMode.ON_SITE)
                .experienceLevel(request.getExperienceLevel())
                .minYearsExperience(request.getMinYearsExperience())
                .salaryMin(request.getSalaryMin())
                .salaryMax(request.getSalaryMax())
                .isSalaryVisible(request.isSalaryVisible())
                .numberOfOpenings(request.getNumberOfOpenings() != null ? request.getNumberOfOpenings() : 1)
                .applicationDeadline(request.getApplicationDeadline())
                .build();

        return JobPostingDto.from(jobPostingRepository.save(job));
    }

    /** Returns all job postings belonging to the recruiter's company. */
    @Transactional(readOnly = true)
    public List<JobPostingDto> getMyJobs(UUID userId) {
        CompanyProfile company = getCompany(userId);
        return jobPostingRepository.findByCompanyIdOrderByCreatedAtDesc(company.getId())
                .stream().map(JobPostingDto::from).toList();
    }

    /** Returns a single job posting owned by the recruiter. */
    @Transactional(readOnly = true)
    public JobPostingDto getMyJob(UUID userId, UUID jobId) {
        CompanyProfile company = getCompany(userId);
        JobPosting job = jobPostingRepository.findByIdAndCompanyId(jobId, company.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Job posting not found"));
        return JobPostingDto.from(job);
    }

    /** Partial update — only non-null fields are applied. Cannot update status here. */
    @Transactional
    public JobPostingDto update(UUID userId, UUID jobId, UpdateJobRequest request) {
        CompanyProfile company = getCompany(userId);
        JobPosting job = jobPostingRepository.findByIdAndCompanyId(jobId, company.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Job posting not found"));

        if (request.getTitle() != null)              job.setTitle(request.getTitle());
        if (request.getDescription() != null)        job.setDescription(request.getDescription());
        if (request.getRequirements() != null)       job.setRequirements(request.getRequirements());
        if (request.getCategory() != null)           job.setCategory(request.getCategory());
        if (request.getLocation() != null)           job.setLocation(request.getLocation());
        if (request.getEmploymentType() != null)     job.setEmploymentType(request.getEmploymentType());
        if (request.getWorkMode() != null)           job.setWorkMode(request.getWorkMode());
        if (request.getExperienceLevel() != null)    job.setExperienceLevel(request.getExperienceLevel());
        if (request.getMinYearsExperience() != null) job.setMinYearsExperience(request.getMinYearsExperience());
        if (request.getSalaryMin() != null)          job.setSalaryMin(request.getSalaryMin());
        if (request.getSalaryMax() != null)          job.setSalaryMax(request.getSalaryMax());
        if (request.getIsSalaryVisible() != null)    job.setSalaryVisible(request.getIsSalaryVisible());
        if (request.getNumberOfOpenings() != null)   job.setNumberOfOpenings(request.getNumberOfOpenings());
        if (request.getApplicationDeadline() != null) job.setApplicationDeadline(request.getApplicationDeadline());

        return JobPostingDto.from(jobPostingRepository.save(job));
    }

    /**
     * Changes job status following allowed transitions:
     * DRAFT → ACTIVE, ACTIVE → PAUSED, PAUSED → ACTIVE, ACTIVE/PAUSED → CLOSED
     */
    @Transactional
    public JobPostingDto changeStatus(UUID userId, UUID jobId, JobStatus newStatus) {
        CompanyProfile company = getVerifiedCompany(userId);
        JobPosting job = jobPostingRepository.findByIdAndCompanyId(jobId, company.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Job posting not found"));

        validateStatusTransition(job.getStatus(), newStatus);

        job.setStatus(newStatus);
        if (newStatus == JobStatus.ACTIVE && job.getPostedAt() == null) {
            job.setPostedAt(LocalDateTime.now());
        }
        if (newStatus == JobStatus.CLOSED) {
            job.setClosedAt(LocalDateTime.now());
        }

        return JobPostingDto.from(jobPostingRepository.save(job));
    }

    /** Deletes a job posting — only allowed when status is DRAFT. */
    @Transactional
    public void delete(UUID userId, UUID jobId) {
        CompanyProfile company = getCompany(userId);
        JobPosting job = jobPostingRepository.findByIdAndCompanyId(jobId, company.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Job posting not found"));

        if (job.getStatus() != JobStatus.DRAFT) {
            throw new BadRequestException("Only DRAFT job postings can be deleted.");
        }
        jobPostingRepository.delete(job);
    }

    // ── Public Search ─────────────────────────────────────────────────────────

    /**
     * Searches active jobs with optional keyword and filters.
     * View count is NOT incremented here — only on detail view.
     */
    @Transactional(readOnly = true)
    public PagedResponse<JobPostingDto> searchJobs(
            String keyword, String location, String category,
            EmploymentType employmentType, WorkMode workMode,
            ExperienceLevel experienceLevel, Integer minSalary,
            int page, int size) {

        Page<JobPosting> result = jobPostingRepository.searchActiveJobs(
                keyword, location, category,
                employmentType != null ? employmentType.name() : null,
                workMode != null ? workMode.name() : null,
                experienceLevel != null ? experienceLevel.name() : null,
                minSalary,
                PageRequest.of(page, size));

        return new PagedResponse<>(result.map(JobPostingDto::from));
    }

    /**
     * Returns full job detail and increments view count.
     * Only returns ACTIVE jobs to candidates.
     */
    @Transactional
    public JobPostingDto getJobDetail(UUID jobId) {
        JobPosting job = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if (job.getStatus() != JobStatus.ACTIVE) {
            throw new ResourceNotFoundException("Job not found");
        }

        jobPostingRepository.incrementViewCount(jobId);
        job.setViewCount(job.getViewCount() + 1);
        return JobPostingDto.from(job);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private CompanyProfile getCompany(UUID userId) {
        return companyProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Company profile not found. Please create one first."));
    }

    private CompanyProfile getVerifiedCompany(UUID userId) {
        CompanyProfile company = getCompany(userId);
        if (company.getVerificationStatus() != VerificationStatus.VERIFIED) {
            throw new ForbiddenException("Your company must be verified by admin before posting jobs.");
        }
        return company;
    }

    private void validateStatusTransition(JobStatus current, JobStatus next) {
        boolean valid = switch (current) {
            case DRAFT   -> next == JobStatus.ACTIVE;
            case ACTIVE  -> next == JobStatus.PAUSED || next == JobStatus.CLOSED;
            case PAUSED  -> next == JobStatus.ACTIVE || next == JobStatus.CLOSED;
            default      -> false;
        };
        if (!valid) {
            throw new BadRequestException(
                    "Cannot change status from " + current + " to " + next);
        }
    }
}
