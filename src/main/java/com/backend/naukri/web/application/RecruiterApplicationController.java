package com.backend.naukri.web.application;

import com.backend.naukri.common.dto.ApiResponse;
import com.backend.naukri.domain.application.dto.ApplicationDetailDto;
import com.backend.naukri.domain.application.dto.UpdateApplicationStatusRequest;
import com.backend.naukri.domain.application.service.JobApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Application management endpoints for recruiters.
 * All endpoints require RECRUITER role.
 */
@RestController
@RequestMapping("/api/v1/recruiter")
@RequiredArgsConstructor
@PreAuthorize("hasRole('RECRUITER')")
public class RecruiterApplicationController {

    private final JobApplicationService applicationService;

    /** List all applicants for a specific job posting. */
    @GetMapping("/jobs/{jobId}/applications")
    public ResponseEntity<ApiResponse<List<ApplicationDetailDto>>> getApplicationsForJob(
            @RequestAttribute("userId") UUID userId,
            @PathVariable UUID jobId) {
        return ResponseEntity.ok(ApiResponse.success(
                applicationService.getApplicationsForJob(userId, jobId)));
    }

    /**
     * View full application detail.
     * Auto-marks status as VIEWED if still in APPLIED state.
     */
    @GetMapping("/applications/{applicationId}")
    public ResponseEntity<ApiResponse<ApplicationDetailDto>> getApplicationDetail(
            @RequestAttribute("userId") UUID userId,
            @PathVariable UUID applicationId) {
        return ResponseEntity.ok(ApiResponse.success(
                applicationService.getApplicationDetail(userId, applicationId)));
    }

    /**
     * Move applicant through the pipeline.
     * Valid transitions: VIEWED → SHORTLISTED → INTERVIEW_SCHEDULED → OFFERED → HIRED / REJECTED
     * Recruiter can also add an internal note (not visible to candidate).
     */
    @PatchMapping("/applications/{applicationId}/status")
    public ResponseEntity<ApiResponse<ApplicationDetailDto>> updateStatus(
            @RequestAttribute("userId") UUID userId,
            @PathVariable UUID applicationId,
            @RequestBody UpdateApplicationStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Status updated",
                applicationService.updateStatus(userId, applicationId, request)));
    }
}
