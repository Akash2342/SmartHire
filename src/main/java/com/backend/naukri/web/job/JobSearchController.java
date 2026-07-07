package com.backend.naukri.web.job;

import com.backend.naukri.common.dto.ApiResponse;
import com.backend.naukri.common.dto.PagedResponse;
import com.backend.naukri.common.enums.EmploymentType;
import com.backend.naukri.common.enums.ExperienceLevel;
import com.backend.naukri.common.enums.WorkMode;
import com.backend.naukri.domain.job.dto.JobPostingDto;
import com.backend.naukri.domain.job.service.JobPostingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Public job search endpoints — no authentication required.
 * Only ACTIVE job postings are returned in all responses.
 */
@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
public class JobSearchController {

    private final JobPostingService jobPostingService;

    /**
     * Search active jobs with optional filters and pagination.
     * All query params are optional — omitting all returns all active jobs.
     *
     * Example: GET /api/v1/jobs?keyword=java&location=Bengaluru&page=0&size=10
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<JobPostingDto>>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) EmploymentType employmentType,
            @RequestParam(required = false) WorkMode workMode,
            @RequestParam(required = false) ExperienceLevel experienceLevel,
            @RequestParam(required = false) Integer minSalary,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PagedResponse<JobPostingDto> result = jobPostingService.searchJobs(
                keyword, location, category,
                employmentType, workMode, experienceLevel, minSalary,
                page, size);

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Returns full job detail including company info.
     * Increments the view count on every call.
     * Returns 404 if job is not in ACTIVE status.
     */
    @GetMapping("/{jobId}")
    public ResponseEntity<ApiResponse<JobPostingDto>> getDetail(@PathVariable UUID jobId) {
        return ResponseEntity.ok(ApiResponse.success(jobPostingService.getJobDetail(jobId)));
    }
}
