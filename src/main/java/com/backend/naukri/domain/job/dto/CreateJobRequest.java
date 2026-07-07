package com.backend.naukri.domain.job.dto;

import com.backend.naukri.common.enums.EmploymentType;
import com.backend.naukri.common.enums.ExperienceLevel;
import com.backend.naukri.common.enums.WorkMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateJobRequest {

    @NotBlank(message = "Job title is required")
    private String title;

    @NotBlank(message = "Job description is required")
    private String description;

    private String requirements;
    private String category;
    private String location;

    @NotNull(message = "Employment type is required")
    private EmploymentType employmentType;

    private WorkMode workMode;

    @NotNull(message = "Experience level is required")
    private ExperienceLevel experienceLevel;

    private Integer minYearsExperience;
    private Integer salaryMin;
    private Integer salaryMax;
    private boolean isSalaryVisible = true;
    private Integer numberOfOpenings;
    private LocalDate applicationDeadline;
}
