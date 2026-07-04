package com.backend.naukri.web.candidate;

import com.backend.naukri.common.dto.ApiResponse;
import com.backend.naukri.domain.candidate.dto.*;
import com.backend.naukri.domain.candidate.service.CandidateProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/candidate/profile")
@RequiredArgsConstructor
public class CandidateProfileController {

    private final CandidateProfileService profileService;

    // ── Profile ──────────────────────────────────────────────────────────────

    @GetMapping
    @PreAuthorize("hasRole('SEEKER')")
    public ResponseEntity<ApiResponse<CandidateProfileDto>> getMyProfile(
            @RequestAttribute("userId") UUID userId) {
        return ResponseEntity.ok(ApiResponse.success(profileService.getMyProfile(userId)));
    }

    @GetMapping("/{profileId}")
    public ResponseEntity<ApiResponse<CandidateProfileDto>> getProfileById(
            @PathVariable UUID profileId) {
        return ResponseEntity.ok(ApiResponse.success(profileService.getProfileById(profileId)));
    }

    @PutMapping
    @PreAuthorize("hasRole('SEEKER')")
    public ResponseEntity<ApiResponse<CandidateProfileDto>> updateProfile(
            @RequestAttribute("userId") UUID userId,
            @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Profile updated", profileService.updateProfile(userId, request)));
    }

    // ── Work Experience ───────────────────────────────────────────────────────

    @PostMapping("/experience")
    @PreAuthorize("hasRole('SEEKER')")
    public ResponseEntity<ApiResponse<CandidateProfileDto>> addExperience(
            @RequestAttribute("userId") UUID userId,
            @Valid @RequestBody WorkExperienceRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Experience added", profileService.addWorkExperience(userId, request)));
    }

    @PutMapping("/experience/{expId}")
    @PreAuthorize("hasRole('SEEKER')")
    public ResponseEntity<ApiResponse<CandidateProfileDto>> updateExperience(
            @RequestAttribute("userId") UUID userId,
            @PathVariable Long expId,
            @Valid @RequestBody WorkExperienceRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Experience updated", profileService.updateWorkExperience(userId, expId, request)));
    }

    @DeleteMapping("/experience/{expId}")
    @PreAuthorize("hasRole('SEEKER')")
    public ResponseEntity<ApiResponse<Void>> deleteExperience(
            @RequestAttribute("userId") UUID userId,
            @PathVariable Long expId) {
        profileService.deleteWorkExperience(userId, expId);
        return ResponseEntity.ok(ApiResponse.success("Experience deleted"));
    }

    // ── Education ─────────────────────────────────────────────────────────────

    @PostMapping("/education")
    @PreAuthorize("hasRole('SEEKER')")
    public ResponseEntity<ApiResponse<CandidateProfileDto>> addEducation(
            @RequestAttribute("userId") UUID userId,
            @Valid @RequestBody EducationRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Education added", profileService.addEducation(userId, request)));
    }

    @PutMapping("/education/{eduId}")
    @PreAuthorize("hasRole('SEEKER')")
    public ResponseEntity<ApiResponse<CandidateProfileDto>> updateEducation(
            @RequestAttribute("userId") UUID userId,
            @PathVariable Long eduId,
            @Valid @RequestBody EducationRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Education updated", profileService.updateEducation(userId, eduId, request)));
    }

    @DeleteMapping("/education/{eduId}")
    @PreAuthorize("hasRole('SEEKER')")
    public ResponseEntity<ApiResponse<Void>> deleteEducation(
            @RequestAttribute("userId") UUID userId,
            @PathVariable Long eduId) {
        profileService.deleteEducation(userId, eduId);
        return ResponseEntity.ok(ApiResponse.success("Education deleted"));
    }

    // ── Skills ────────────────────────────────────────────────────────────────

    @PostMapping("/skills")
    @PreAuthorize("hasRole('SEEKER')")
    public ResponseEntity<ApiResponse<CandidateProfileDto>> addSkill(
            @RequestAttribute("userId") UUID userId,
            @Valid @RequestBody AddSkillRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Skill added", profileService.addSkill(userId, request)));
    }

    @DeleteMapping("/skills/{skillId}")
    @PreAuthorize("hasRole('SEEKER')")
    public ResponseEntity<ApiResponse<Void>> deleteSkill(
            @RequestAttribute("userId") UUID userId,
            @PathVariable Long skillId) {
        profileService.deleteSkill(userId, skillId);
        return ResponseEntity.ok(ApiResponse.success("Skill deleted"));
    }
}
