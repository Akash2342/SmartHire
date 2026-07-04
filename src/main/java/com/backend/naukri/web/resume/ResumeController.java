package com.backend.naukri.web.resume;

import com.backend.naukri.common.dto.ApiResponse;
import com.backend.naukri.domain.resume.dto.ResumeDto;
import com.backend.naukri.domain.resume.entity.Resume;
import com.backend.naukri.domain.resume.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/candidate/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('SEEKER')")
    public ResponseEntity<ApiResponse<ResumeDto>> upload(
            @RequestAttribute("userId") UUID userId,
            @RequestParam("file") MultipartFile file) throws IOException {
        ResumeDto dto = resumeService.upload(userId, file);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Resume uploaded successfully", dto));
    }

    @GetMapping
    @PreAuthorize("hasRole('SEEKER')")
    public ResponseEntity<ApiResponse<ResumeDto>> getResumeInfo(
            @RequestAttribute("userId") UUID userId) {
        return ResponseEntity.ok(ApiResponse.success(resumeService.getResumeInfo(userId)));
    }

    @GetMapping("/download")
    @PreAuthorize("hasRole('SEEKER')")
    public ResponseEntity<byte[]> download(
            @RequestAttribute("userId") UUID userId) {
        Resume resume = resumeService.getResumeForDownload(userId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(resume.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resume.getOriginalFileName() + "\"")
                .body(resume.getFileData());
    }

    @DeleteMapping
    @PreAuthorize("hasRole('SEEKER')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @RequestAttribute("userId") UUID userId) {
        resumeService.delete(userId);
        return ResponseEntity.ok(ApiResponse.success("Resume deleted. You can now upload a new one."));
    }
}
