package com.backend.naukri.domain.resume.service;

import com.backend.naukri.domain.candidate.entity.CandidateProfile;
import com.backend.naukri.domain.candidate.repository.CandidateProfileRepository;
import com.backend.naukri.domain.resume.dto.ResumeDto;
import com.backend.naukri.domain.resume.entity.Resume;
import com.backend.naukri.domain.resume.repository.ResumeRepository;
import com.backend.naukri.exception.BadRequestException;
import com.backend.naukri.exception.ConflictException;
import com.backend.naukri.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Manages resume upload, retrieval, and deletion.
 * Only one resume allowed per candidate — must delete before uploading a new one.
 * File is stored as BYTEA in PostgreSQL (no filesystem dependency).
 */
@Service
@RequiredArgsConstructor
public class ResumeService {

    private static final List<String> ALLOWED_TYPES = List.of(
            "application/pdf",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );
    private static final long MAX_SIZE_BYTES = 5 * 1024 * 1024; // 5MB

    private final ResumeRepository resumeRepository;
    private final CandidateProfileRepository candidateProfileRepository;

    /**
     * Uploads a resume for the candidate.
     * Rejects if a resume already exists, file type is invalid, or size exceeds 5MB.
     */
    @Transactional
    public ResumeDto upload(UUID userId, MultipartFile file) throws IOException {
        CandidateProfile profile = findProfile(userId);

        if (resumeRepository.existsByCandidateProfileId(profile.getId())) {
            throw new ConflictException("Resume already exists. Delete it first to upload a new one.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new BadRequestException("Only PDF and DOCX files are allowed.");
        }

        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new BadRequestException("File size must not exceed 5MB.");
        }

        Resume resume = Resume.builder()
                .candidateProfile(profile)
                .originalFileName(file.getOriginalFilename())
                .fileType(contentType)
                .fileSizeBytes(file.getSize())
                .fileData(file.getBytes())
                .build();

        return ResumeDto.from(resumeRepository.save(resume));
    }

    /** Returns resume metadata (no file bytes) for display purposes. */
    @Transactional(readOnly = true)
    public ResumeDto getResumeInfo(UUID userId) {
        CandidateProfile profile = findProfile(userId);
        Resume resume = findResume(profile.getId());
        return ResumeDto.from(resume);
    }

    /** Returns full resume entity including file bytes — used for download streaming. */
    @Transactional(readOnly = true)
    public Resume getResumeForDownload(UUID userId) {
        CandidateProfile profile = findProfile(userId);
        return findResume(profile.getId());
    }

    /** Deletes the resume. Candidate can then upload a new one. */
    @Transactional
    public void delete(UUID userId) {
        CandidateProfile profile = findProfile(userId);
        Resume resume = findResume(profile.getId());
        resumeRepository.delete(resume);
    }

    private CandidateProfile findProfile(UUID userId) {
        return candidateProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate profile not found"));
    }

    private Resume findResume(UUID profileId) {
        return resumeRepository.findByCandidateProfileId(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("No resume found. Please upload one."));
    }
}
