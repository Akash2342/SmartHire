package com.backend.naukri.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/** Platform-wide metrics shown on admin dashboard. */
@Data
@AllArgsConstructor
public class DashboardDto {
    private long totalCandidates;
    private long totalRecruiters;
    private long totalActiveJobs;
    private long totalApplications;
    private long pendingCompanyVerifications;
}
