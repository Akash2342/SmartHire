package com.backend.naukri.domain.candidate.dto;

import com.backend.naukri.common.enums.EmploymentType;
import com.backend.naukri.common.enums.NoticePeriod;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String firstName;
    private String lastName;
    private String phone;
    private String city;
    private String headline;
    private String summary;
    private String employmentStatus;
    private NoticePeriod noticePeriod;
    private Integer expectedSalaryMin;
    private Integer expectedSalaryMax;
    private EmploymentType preferredJobType;
    private String linkedinUrl;
    private String githubUrl;
    private String portfolioUrl;
}
