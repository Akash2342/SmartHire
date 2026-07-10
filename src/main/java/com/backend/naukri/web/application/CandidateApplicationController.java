package com.backend.naukri.web.application;

import com.backend.naukri.common.dto.ApiResponse;
import com.backend.naukri.domain.application.dto.ApplyRequest;
import com.backend.naukri.domain.application.dto.ApplicationDto;
import com.backend.naukri.domain.application.service.JobApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Job application endpoints for candidates.
 * All endpoints require SEEKER role.
 */
@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SEEKER')")
public class CandidateApplicationController {

    private final JobApplicationService applicationService;

    /** Apply to a job. Resume must be uploaded first. */
    @PostMapping
    public ResponseEntity<ApiResponse<ApplicationDto>> apply(
            @RequestAttribute("userId") UUID userId,
            @Valid @RequestBody ApplyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Application submitted successfully",
                        applicationService.apply(userId, request)));
    }

    /** List all applications submitted by the candidate. */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ApplicationDto>>> getMyApplications(
            @RequestAttribute("userId") UUID userId) {
        return ResponseEntity.ok(ApiResponse.success(
                applicationService.getMyApplications(userId)));
    }

    /** Withdraw an application. Only allowed when status is APPLIED. */
    @DeleteMapping("/{applicationId}")
    public ResponseEntity<ApiResponse<Void>> withdraw(
            @RequestAttribute("userId") UUID userId,
            @PathVariable UUID applicationId) {
        applicationService.withdraw(userId, applicationId);
        return ResponseEntity.ok(ApiResponse.success("Application withdrawn successfully"));
    }
}
