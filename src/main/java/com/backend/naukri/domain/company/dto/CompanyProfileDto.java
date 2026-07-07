package com.backend.naukri.domain.company.dto;

import com.backend.naukri.common.enums.VerificationStatus;
import com.backend.naukri.domain.company.entity.CompanyProfile;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CompanyProfileDto {

    private UUID id;
    private String recruiterEmail;
    private String companyName;
    private String displayName;
    private String industry;
    private String companySize;
    private Integer foundedYear;
    private String websiteUrl;
    private String description;
    private String headquartersCity;
    private boolean hasLogo;
    private VerificationStatus verificationStatus;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CompanyProfileDto from(CompanyProfile c) {
        CompanyProfileDto dto = new CompanyProfileDto();
        dto.setId(c.getId());
        dto.setRecruiterEmail(c.getUser().getEmail());
        dto.setCompanyName(c.getCompanyName());
        dto.setDisplayName(c.getDisplayName());
        dto.setIndustry(c.getIndustry());
        dto.setCompanySize(c.getCompanySize());
        dto.setFoundedYear(c.getFoundedYear());
        dto.setWebsiteUrl(c.getWebsiteUrl());
        dto.setDescription(c.getDescription());
        dto.setHeadquartersCity(c.getHeadquartersCity());
        dto.setHasLogo(c.getLogo() != null);
        dto.setVerificationStatus(c.getVerificationStatus());
        dto.setRejectionReason(c.getRejectionReason());
        dto.setCreatedAt(c.getCreatedAt());
        dto.setUpdatedAt(c.getUpdatedAt());
        return dto;
    }
}
