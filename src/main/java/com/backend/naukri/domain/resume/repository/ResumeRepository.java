package com.backend.naukri.domain.resume.repository;

import com.backend.naukri.domain.resume.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ResumeRepository extends JpaRepository<Resume, UUID> {
    Optional<Resume> findByCandidateProfileId(UUID candidateProfileId);
    boolean existsByCandidateProfileId(UUID candidateProfileId);
}
