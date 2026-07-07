package com.backend.naukri.domain.company.dto;

import lombok.Data;

@Data
public class UpdateCompanyRequest {
    private String companyName;
    private String displayName;
    private String industry;
    private String companySize;
    private Integer foundedYear;
    private String websiteUrl;
    private String description;
    private String headquartersCity;
}
