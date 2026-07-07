package com.backend.naukri.web.job;

import com.backend.naukri.common.dto.ApiResponse;
import com.backend.naukri.common.enums.JobStatus;
import com.backend.naukri.domain.job.dto.CreateJobRequest;
import com.backend.naukri.domain.job.dto.JobPostingDto;
import com.backend.naukri.domain.job.dto.UpdateJobRequest;
import com.backend.naukri.domain.job.service.JobPostingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Job posting management endpoints for recruiters.
 * All endpoints require RECRUITER role.
 * Company must be VERIFIED to create or publish jobs.
 */
@RestController
@RequestMapping("/api/v1/recruiter/jobs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('RECRUITER')")
public class RecruiterJobController {

    private final JobPostingService jobPostingService;

    /** Creates a new job posting in DRAFT status. Must be published separately. */
    @PostMapping
    public ResponseEntity<ApiResponse<JobPostingDto>> create(
            @RequestAttribute("userId") UUID userId,
            @Valid @RequestBody CreateJobRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Job posting created as DRAFT",
                        jobPostingService.create(userId, request)));
    }

    /** Returns all job postings belonging to the recruiter's company. */
    @GetMapping
    public ResponseEntity<ApiResponse<List<JobPostingDto>>> getMyJobs(
            @RequestAttribute("userId") UUID userId) {
        return ResponseEntity.ok(ApiResponse.success(jobPostingService.getMyJobs(userId)));
    }

    /** Returns a single job posting. Only accessible by the owning recruiter. */
    @GetMapping("/{jobId}")
    public ResponseEntity<ApiResponse<JobPostingDto>> getMyJob(
            @RequestAttribute("userId") UUID userId,
            @PathVariable UUID jobId) {
        return ResponseEntity.ok(ApiResponse.success(jobPostingService.getMyJob(userId, jobId)));
    }

    /**
     * Partial update — only fields provided in the request body are changed.
     * Status cannot be changed here — use the /status endpoint instead.
     */
    @PutMapping("/{jobId}")
    public ResponseEntity<ApiResponse<JobPostingDto>> update(
            @RequestAttribute("userId") UUID userId,
            @PathVariable UUID jobId,
            @RequestBody UpdateJobRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Job posting updated",
                jobPostingService.update(userId, jobId, request)));
    }

    /**
     * Change job status following allowed transitions:
     * DRAFT → ACTIVE, ACTIVE → PAUSED, PAUSED → ACTIVE, ACTIVE/PAUSED → CLOSED
     */
    @PatchMapping("/{jobId}/status")
    public ResponseEntity<ApiResponse<JobPostingDto>> changeStatus(
            @RequestAttribute("userId") UUID userId,
            @PathVariable UUID jobId,
            @RequestParam JobStatus status) {
        return ResponseEntity.ok(ApiResponse.success("Status updated to " + status,
                jobPostingService.changeStatus(userId, jobId, status)));
    }

    /** Permanently deletes a job posting. Only allowed when status is DRAFT. */
    @DeleteMapping("/{jobId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @RequestAttribute("userId") UUID userId,
            @PathVariable UUID jobId) {
        jobPostingService.delete(userId, jobId);
        return ResponseEntity.ok(ApiResponse.success("Job posting deleted"));
    }
}
