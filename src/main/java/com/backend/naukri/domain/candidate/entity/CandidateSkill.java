package com.backend.naukri.domain.candidate.entity;

import com.backend.naukri.common.enums.ProficiencyLevel;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "candidate_skills",
       uniqueConstraints = @UniqueConstraint(columnNames = {"candidate_profile_id", "skill_name"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_profile_id", nullable = false)
    private CandidateProfile candidateProfile;

    @Column(nullable = false)
    private String skillName;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ProficiencyLevel proficiency = ProficiencyLevel.INTERMEDIATE;

    private Integer yearsOfExperience;
}
