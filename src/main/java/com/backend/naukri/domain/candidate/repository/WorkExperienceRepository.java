package com.backend.naukri.domain.candidate.repository;

import com.backend.naukri.domain.candidate.entity.WorkExperience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WorkExperienceRepository extends JpaRepository<WorkExperience, Long> {
    Optional<WorkExperience> findByIdAndCandidateProfileId(Long id, UUID candidateProfileId);
}
