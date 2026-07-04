package com.backend.naukri.domain.candidate.repository;

import com.backend.naukri.domain.candidate.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EducationRepository extends JpaRepository<Education, Long> {
    Optional<Education> findByIdAndCandidateProfileId(Long id, UUID candidateProfileId);
}
