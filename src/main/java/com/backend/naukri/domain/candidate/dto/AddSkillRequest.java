package com.backend.naukri.domain.candidate.dto;

import com.backend.naukri.common.enums.ProficiencyLevel;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddSkillRequest {

    @NotBlank(message = "Skill name is required")
    private String skillName;

    private ProficiencyLevel proficiency = ProficiencyLevel.INTERMEDIATE;
    private Integer yearsOfExperience;
}
