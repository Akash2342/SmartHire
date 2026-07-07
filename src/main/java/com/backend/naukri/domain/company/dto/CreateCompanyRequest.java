package com.backend.naukri.domain.company.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCompanyRequest {

    @NotBlank(message = "Company name is required")
    private String companyName;

    private String displayName;
    private String industry;
    private String companySize;
    private Integer foundedYear;
    private String websiteUrl;
    private String description;
    private String headquartersCity;
}
