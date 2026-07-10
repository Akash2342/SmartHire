package com.backend.naukri.web.admin;

import com.backend.naukri.common.dto.ApiResponse;
import com.backend.naukri.common.dto.PagedResponse;
import com.backend.naukri.common.enums.Role;
import com.backend.naukri.common.enums.VerificationStatus;
import com.backend.naukri.domain.admin.dto.*;
import com.backend.naukri.domain.admin.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Admin management endpoints — user control, company verification, dashboard.
 * All endpoints require ADMIN role.
 */
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    // ── User Management ───────────────────────────────────────────────────────

    /**
     * List all users with optional role filter and pagination.
     * GET /api/v1/admin/users?role=SEEKER&page=0&size=20
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<PagedResponse<UserSummaryDto>>> getUsers(
            @RequestParam(required = false) Role role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(adminService.getUsers(role, page, size)));
    }

    /**
     * Activate or deactivate a user account.
     * PATCH /api/v1/admin/users/{userId}/status?active=false
     */
    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<ApiResponse<UserSummaryDto>> updateUserStatus(
            @RequestAttribute("userId") UUID adminUserId,
            @PathVariable UUID userId,
            @RequestParam boolean active) {
        return ResponseEntity.ok(ApiResponse.success(
                active ? "User activated" : "User deactivated",
                adminService.updateUserStatus(adminUserId, userId, active)));
    }

    // ── Company Verification ──────────────────────────────────────────────────

    /** List all companies — optionally filtered by verification status. */
    @GetMapping("/companies")
    public ResponseEntity<ApiResponse<List<CompanySummaryDto>>> getCompanies(
            @RequestParam(required = false) VerificationStatus status) {
        List<CompanySummaryDto> result = status != null
                ? adminService.getCompaniesByStatus(status)
                : adminService.getAllCompanies();
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Approve or reject a company verification request.
     * Body: { "action": "APPROVE" } or { "action": "REJECT", "rejectionReason": "..." }
     */
    @PatchMapping("/companies/{companyId}/verify")
    public ResponseEntity<ApiResponse<CompanySummaryDto>> verifyCompany(
            @PathVariable UUID companyId,
            @Valid @RequestBody VerifyCompanyRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Company verification updated",
                adminService.verifyCompany(companyId, request)));
    }

    // ── Dashboard ─────────────────────────────────────────────────────────────

    /** Returns platform-wide metrics: user counts, active jobs, applications, pending verifications. */
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardDto>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getDashboard()));
    }
}
