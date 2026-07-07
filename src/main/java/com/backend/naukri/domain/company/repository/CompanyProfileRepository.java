package com.backend.naukri.domain.company.repository;

import com.backend.naukri.domain.company.entity.CompanyProfile;
import com.backend.naukri.common.enums.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanyProfileRepository extends JpaRepository<CompanyProfile, UUID> {
    Optional<CompanyProfile> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
    List<CompanyProfile> findAllByVerificationStatus(VerificationStatus status);
}
