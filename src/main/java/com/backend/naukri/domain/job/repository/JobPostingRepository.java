package com.backend.naukri.domain.job.repository;

import com.backend.naukri.common.enums.*;
import com.backend.naukri.domain.job.entity.JobPosting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JobPostingRepository extends JpaRepository<JobPosting, UUID> {

    /** Returns all jobs for a company ordered by newest first — used in recruiter dashboard. */
    List<JobPosting> findByCompanyIdOrderByCreatedAtDesc(UUID companyId);

    /** Finds a job only if it belongs to the given company — prevents cross-recruiter access. */
    Optional<JobPosting> findByIdAndCompanyId(UUID id, UUID companyId);

    /**
     * Native SQL search across active jobs with optional filters.
     * Uses CAST(:param AS TEXT) IS NULL pattern to handle optional enum params
     * because PostgreSQL cannot compare a null string param to an enum column directly.
     * Enum values are passed as strings from the service layer.
     */
    @Query(value = """
            SELECT * FROM job_postings j
            WHERE j.status = 'ACTIVE'
            AND (:keyword IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                 OR LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
            AND (:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%')))
            AND (:category IS NULL OR LOWER(j.category) = LOWER(:category))
            AND (CAST(:employmentType AS TEXT) IS NULL OR j.employment_type = :employmentType)
            AND (CAST(:workMode AS TEXT) IS NULL OR j.work_mode = :workMode)
            AND (CAST(:experienceLevel AS TEXT) IS NULL OR j.experience_level = :experienceLevel)
            AND (:minSalary IS NULL OR j.salary_max >= :minSalary)
            ORDER BY j.posted_at DESC
            """, nativeQuery = true)
    Page<JobPosting> searchActiveJobs(
            @Param("keyword") String keyword,
            @Param("location") String location,
            @Param("category") String category,
            @Param("employmentType") String employmentType,
            @Param("workMode") String workMode,
            @Param("experienceLevel") String experienceLevel,
            @Param("minSalary") Integer minSalary,
            Pageable pageable);

    /**
     * Increments view count directly in DB without loading the entity.
     * Avoids unnecessary read + write cycle on every job detail page view.
     */
    @Modifying
    @Query("UPDATE JobPosting j SET j.viewCount = j.viewCount + 1 WHERE j.id = :id")
    void incrementViewCount(@Param("id") UUID id);
}
