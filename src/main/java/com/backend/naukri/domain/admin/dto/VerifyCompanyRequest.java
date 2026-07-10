package com.backend.naukri.domain.admin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VerifyCompanyRequest {

    @NotNull(message = "Action is required")
    private Action action;

    /** Required when action is REJECT. */
    private String rejectionReason;

    public enum Action {
        APPROVE, REJECT
    }
}
