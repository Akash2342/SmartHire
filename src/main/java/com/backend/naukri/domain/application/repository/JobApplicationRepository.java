package com.backend.naukri.domain.application.repository;

import com.backend.naukri.domain.application.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JobApplicationRepository extends JpaRepository<JobApplication, UUID> {

    /** All applications submitted by a candidate — for candidate dashboard. */
    List<JobApplication> findByCandidateProfileIdOrderByAppliedAtDesc(UUID candidateProfileId);

    /** All applications received for a job — for recruiter pipeline view. */
    List<JobApplication> findByJobIdOrderByAppliedAtDesc(UUID jobId);

    /** Check if candidate already applied to this job — prevents duplicates. */
    boolean existsByJobIdAndCandidateProfileId(UUID jobId, UUID candidateProfileId);

    /** Find specific application owned by a candidate — used for withdraw. */
    Optional<JobApplication> findByIdAndCandidateProfileId(UUID id, UUID candidateProfileId);

    /** Find specific application under a recruiter's job — used for status update. */
    Optional<JobApplication> findByIdAndJobCompanyId(UUID id, UUID companyId);
}
