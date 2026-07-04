package com.backend.naukri.domain.candidate.repository;

import com.backend.naukri.domain.candidate.entity.CandidateSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CandidateSkillRepository extends JpaRepository<CandidateSkill, Long> {
    Optional<CandidateSkill> findByIdAndCandidateProfileId(Long id, UUID candidateProfileId);
    boolean existsByCandidateProfileIdAndSkillName(UUID candidateProfileId, String skillName);
}
