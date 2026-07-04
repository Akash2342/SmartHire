package com.backend.naukri.domain.candidate.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "educations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Education {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_profile_id", nullable = false)
    private CandidateProfile candidateProfile;

    @Column(nullable = false)
    private String institutionName;

    @Column(nullable = false)
    private String degree;

    private String fieldOfStudy;
    private Integer startYear;
    private Integer endYear;

    @Builder.Default
    private boolean isCurrent = false;

    private String grade;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
