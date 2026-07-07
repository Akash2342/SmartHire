package com.backend.naukri.web.company;

import com.backend.naukri.common.dto.ApiResponse;
import com.backend.naukri.domain.company.dto.CompanyProfileDto;
import com.backend.naukri.domain.company.dto.CreateCompanyRequest;
import com.backend.naukri.domain.company.dto.UpdateCompanyRequest;
import com.backend.naukri.domain.company.entity.CompanyProfile;
import com.backend.naukri.domain.company.service.CompanyProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * Handles company profile operations for recruiters.
 * Create/update requires RECRUITER role.
 * Public view (by ID) is open to all.
 */
@RestController
@RequestMapping("/api/v1/company/profile")
@RequiredArgsConstructor
public class CompanyProfileController {

    private final CompanyProfileService companyProfileService;

    /** Create a new company profile. Only one allowed per recruiter. */
    @PostMapping
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApiResponse<CompanyProfileDto>> create(
            @RequestAttribute("userId") UUID userId,
            @Valid @RequestBody CreateCompanyRequest request) {
        CompanyProfileDto dto = companyProfileService.create(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Company profile created. Pending admin verification.", dto));
    }

    /** Get own company profile. */
    @GetMapping
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApiResponse<CompanyProfileDto>> getMyProfile(
            @RequestAttribute("userId") UUID userId) {
        return ResponseEntity.ok(ApiResponse.success(companyProfileService.getMyProfile(userId)));
    }

    /** Public — view any company by ID (shown on job listings). */
    @GetMapping("/{companyId}")
    public ResponseEntity<ApiResponse<CompanyProfileDto>> getById(
            @PathVariable UUID companyId) {
        return ResponseEntity.ok(ApiResponse.success(companyProfileService.getById(companyId)));
    }

    /** Update company details. Verification status cannot be changed here. */
    @PutMapping
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApiResponse<CompanyProfileDto>> update(
            @RequestAttribute("userId") UUID userId,
            @RequestBody UpdateCompanyRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Company profile updated",
                companyProfileService.update(userId, request)));
    }

    /** Upload or replace company logo. Accepts JPEG, PNG, WEBP up to 2MB. */
    @PostMapping(value = "/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApiResponse<CompanyProfileDto>> uploadLogo(
            @RequestAttribute("userId") UUID userId,
            @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(ApiResponse.success("Logo uploaded",
                companyProfileService.uploadLogo(userId, file)));
    }

    /** Stream the company logo image. */
    @GetMapping("/{companyId}/logo")
    public ResponseEntity<byte[]> getLogo(@PathVariable UUID companyId) {
        CompanyProfile company = companyProfileService.getCompanyWithLogo(companyId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(company.getLogoContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(company.getLogo());
    }
}
