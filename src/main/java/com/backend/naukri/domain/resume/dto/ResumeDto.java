package com.backend.naukri.domain.resume.dto;

import com.backend.naukri.domain.resume.entity.Resume;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ResumeDto {
    private UUID id;
    private String originalFileName;
    private String fileType;
    private Long fileSizeBytes;
    private LocalDateTime uploadedAt;

    public static ResumeDto from(Resume resume) {
        ResumeDto dto = new ResumeDto();
        dto.setId(resume.getId());
        dto.setOriginalFileName(resume.getOriginalFileName());
        dto.setFileType(resume.getFileType());
        dto.setFileSizeBytes(resume.getFileSizeBytes());
        dto.setUploadedAt(resume.getUploadedAt());
        return dto;
    }
}
