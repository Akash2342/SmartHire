package com.backend.naukri.domain.company.entity;

import com.backend.naukri.common.enums.VerificationStatus;
import com.backend.naukri.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a company profile created by a RECRUITER.
 * One recruiter can only have one company profile.
 * Jobs can only be posted once the company is VERIFIED by admin.
 */
@Entity
@Table(name = "company_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String companyName;

    private String displayName;
    private String industry;
    private String companySize;
    private Integer foundedYear;
    private String websiteUrl;

    @Column(length = 2000)
    private String description;

    private String headquartersCity;

    @Column(columnDefinition = "BYTEA")
    private byte[] logo;

    private String logoContentType;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    private String rejectionReason;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
