package com.backend.naukri.domain.admin.service;

import com.backend.naukri.common.dto.PagedResponse;
import com.backend.naukri.common.enums.JobStatus;
import com.backend.naukri.common.enums.Role;
import com.backend.naukri.common.enums.VerificationStatus;
import com.backend.naukri.domain.admin.dto.*;
import com.backend.naukri.domain.company.entity.CompanyProfile;
import com.backend.naukri.domain.company.repository.CompanyProfileRepository;
import com.backend.naukri.domain.application.repository.JobApplicationRepository;
import com.backend.naukri.domain.job.repository.JobPostingRepository;
import com.backend.naukri.domain.user.entity.User;
import com.backend.naukri.domain.user.repository.UserRepository;
import com.backend.naukri.exception.BadRequestException;
import com.backend.naukri.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Admin operations: user management, company verification, platform metrics.
 * No new entities — reuses existing repositories.
 */
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final JobPostingRepository jobPostingRepository;
    private final JobApplicationRepository applicationRepository;

    // ── User Management ───────────────────────────────────────────────────────

    /**
     * Returns paginated list of all users.
     * Optionally filtered by role (SEEKER / RECRUITER).
     */
    @Transactional(readOnly = true)
    public PagedResponse<UserSummaryDto> getUsers(Role role, int page, int size) {
        var pageable = PageRequest.of(page, size);
        var result = role != null
                ? userRepository.findByRoleOrderByCreatedAtDesc(role, pageable)
                : userRepository.findAllByOrderByCreatedAtDesc(pageable);
        return new PagedResponse<>(result.map(UserSummaryDto::from));
    }

    /**
     * Activates or deactivates a user account.
     * Admin cannot deactivate their own account.
     */
    @Transactional
    public UserSummaryDto updateUserStatus(UUID adminUserId, UUID targetUserId, boolean active) {
        if (adminUserId.equals(targetUserId)) {
            throw new BadRequestException("You cannot change your own account status.");
        }

        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setActive(active);
        return UserSummaryDto.from(userRepository.save(user));
    }

    // ── Company Verification ──────────────────────────────────────────────────

    /** Returns all companies with a given verification status. */
    @Transactional(readOnly = true)
    public List<CompanySummaryDto> getCompaniesByStatus(VerificationStatus status) {
        return companyProfileRepository.findAllByVerificationStatus(status)
                .stream().map(CompanySummaryDto::from).toList();
    }

    /** Returns all companies regardless of status. */
    @Transactional(readOnly = true)
    public List<CompanySummaryDto> getAllCompanies() {
        return companyProfileRepository.findAll()
                .stream().map(CompanySummaryDto::from).toList();
    }

    /**
     * Approves or rejects a company verification request.
     * Rejection requires a reason so recruiter knows what to fix.
     */
    @Transactional
    public CompanySummaryDto verifyCompany(UUID companyId, VerifyCompanyRequest request) {
        CompanyProfile company = companyProfileRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        if (request.getAction() == VerifyCompanyRequest.Action.APPROVE) {
            company.setVerificationStatus(VerificationStatus.VERIFIED);
            company.setRejectionReason(null);
        } else {
            if (request.getRejectionReason() == null || request.getRejectionReason().isBlank()) {
                throw new BadRequestException("Rejection reason is required.");
            }
            company.setVerificationStatus(VerificationStatus.REJECTED);
            company.setRejectionReason(request.getRejectionReason());
        }

        return CompanySummaryDto.from(companyProfileRepository.save(company));
    }

    // ── Dashboard Metrics ─────────────────────────────────────────────────────

    /** Returns platform-wide counts for the admin dashboard. */
    @Transactional(readOnly = true)
    public DashboardDto getDashboard() {
        return new DashboardDto(
                userRepository.countByRole(Role.SEEKER),
                userRepository.countByRole(Role.RECRUITER),
                jobPostingRepository.countByStatus(JobStatus.ACTIVE),
                applicationRepository.count(),
                companyProfileRepository.countByVerificationStatus(VerificationStatus.PENDING)
        );
    }
}
