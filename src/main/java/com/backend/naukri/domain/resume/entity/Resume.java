package com.backend.naukri.domain.resume.entity;

import com.backend.naukri.domain.candidate.entity.CandidateProfile;
import jakarta.persistence.*;import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "resumes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_profile_id", nullable = false, unique = true)
    private CandidateProfile candidateProfile;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private String fileType;

    @Column(nullable = false)
    private Long fileSizeBytes;

    @Column(nullable = false, columnDefinition = "BYTEA")
    private byte[] fileData;

    @CreationTimestamp
    private LocalDateTime uploadedAt;
}
