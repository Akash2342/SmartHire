package com.backend.naukri.domain.job.dto;

import com.backend.naukri.common.enums.EmploymentType;
import com.backend.naukri.common.enums.ExperienceLevel;
import com.backend.naukri.common.enums.WorkMode;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateJobRequest {
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
    private Boolean isSalaryVisible;
    private Integer numberOfOpenings;
    private LocalDate applicationDeadline;
}
