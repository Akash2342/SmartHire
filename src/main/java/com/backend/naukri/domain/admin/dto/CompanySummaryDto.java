package com.backend.naukri.domain.admin.dto;

import com.backend.naukri.common.enums.VerificationStatus;
import com.backend.naukri.domain.company.entity.CompanyProfile;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/** Summary of a company shown in the admin verification list. */
@Data
public class CompanySummaryDto {

    private UUID id;
    private UUID ownerUserId;
    private String ownerEmail;
    private String companyName;
    private String displayName;
    private String industry;
    private String website;
    private String companySize;
    private VerificationStatus verificationStatus;
    private String rejectionReason;
    private LocalDateTime createdAt;

    public static CompanySummaryDto from(CompanyProfile company) {
        CompanySummaryDto dto = new CompanySummaryDto();
        dto.setId(company.getId());
        dto.setOwnerUserId(company.getUser().getId());
        dto.setOwnerEmail(company.getUser().getEmail());
        dto.setCompanyName(company.getCompanyName());
        dto.setDisplayName(company.getDisplayName());
        dto.setIndustry(company.getIndustry());
        dto.setWebsite(company.getWebsiteUrl());
        dto.setCompanySize(company.getCompanySize());
        dto.setVerificationStatus(company.getVerificationStatus());
        dto.setRejectionReason(company.getRejectionReason());
        dto.setCreatedAt(company.getCreatedAt());
        return dto;
    }
}
