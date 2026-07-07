package com.backend.naukri.domain.job.dto;

import com.backend.naukri.common.enums.*;
import com.backend.naukri.domain.job.entity.JobPosting;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for job postings.
 * Salary fields (salaryMin, salaryMax) are only populated when isSalaryVisible is true —
 * recruiter can choose to hide salary from candidates.
 */
@Data
public class JobPostingDto {

    private UUID id;
    private UUID companyId;
    private String companyName;
    private String companyDisplayName;
    private boolean companyHasLogo;
    private String title;
    private String description;
    private String requirements;
    private String category;
    private String location;
    private EmploymentType employmentType;
    private WorkMode workMode;
    private ExperienceLevel experienceLevel;
    private Integer minYearsExperience;
    private Integer salaryMin;
    private Integer salaryMax;
    private String salaryCurrency;
    private boolean isSalaryVisible;
    private Integer numberOfOpenings;
    private LocalDate applicationDeadline;
    private JobStatus status;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime postedAt;

    public static JobPostingDto from(JobPosting j) {
        JobPostingDto dto = new JobPostingDto();
        dto.setId(j.getId());
        dto.setCompanyId(j.getCompany().getId());
        dto.setCompanyName(j.getCompany().getCompanyName());
        dto.setCompanyDisplayName(j.getCompany().getDisplayName());
        dto.setCompanyHasLogo(j.getCompany().getLogo() != null);
        dto.setTitle(j.getTitle());
        dto.setDescription(j.getDescription());
        dto.setRequirements(j.getRequirements());
        dto.setCategory(j.getCategory());
        dto.setLocation(j.getLocation());
        dto.setEmploymentType(j.getEmploymentType());
        dto.setWorkMode(j.getWorkMode());
        dto.setExperienceLevel(j.getExperienceLevel());
        dto.setMinYearsExperience(j.getMinYearsExperience());
        dto.setSalaryVisible(j.isSalaryVisible());
        // Only expose salary if recruiter has made it visible
        if (j.isSalaryVisible()) {
            dto.setSalaryMin(j.getSalaryMin());
            dto.setSalaryMax(j.getSalaryMax());
            dto.setSalaryCurrency(j.getSalaryCurrency());
        }
        dto.setNumberOfOpenings(j.getNumberOfOpenings());
        dto.setApplicationDeadline(j.getApplicationDeadline());
        dto.setStatus(j.getStatus());
        dto.setViewCount(j.getViewCount());
        dto.setCreatedAt(j.getCreatedAt());
        dto.setPostedAt(j.getPostedAt());
        return dto;
    }
}
