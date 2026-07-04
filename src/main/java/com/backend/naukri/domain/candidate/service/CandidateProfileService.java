package com.backend.naukri.domain.candidate.service;

import com.backend.naukri.domain.candidate.dto.*;
import com.backend.naukri.domain.candidate.entity.*;
import com.backend.naukri.domain.candidate.repository.*;
import com.backend.naukri.domain.user.entity.User;
import com.backend.naukri.exception.ConflictException;
import com.backend.naukri.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Manages candidate profile data including personal details,
 * work experience, education, and skills.
 * Profile score is recalculated on every update.
 */
@Service
@RequiredArgsConstructor
public class CandidateProfileService {

    private final CandidateProfileRepository profileRepository;
    private final WorkExperienceRepository workExperienceRepository;
    private final EducationRepository educationRepository;
    private final CandidateSkillRepository skillRepository;

    /** Called from AuthService when a SEEKER registers — creates a blank profile. */
    public void createEmptyProfile(User user) {
        CandidateProfile profile = CandidateProfile.builder()
                .user(user)
                .build();
        profileRepository.save(profile);
    }

    // ── Profile ──────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public CandidateProfileDto getMyProfile(UUID userId) {
        return CandidateProfileDto.from(findProfileByUserId(userId));
    }

    @Transactional(readOnly = true)
    public CandidateProfileDto getProfileById(UUID profileId) {
        CandidateProfile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        return CandidateProfileDto.from(profile);
    }

    /** Partial update — only fields present in the request are updated. */
    @Transactional
    public CandidateProfileDto updateProfile(UUID userId, UpdateProfileRequest request) {
        CandidateProfile profile = findProfileByUserId(userId);
        if (request.getFirstName() != null)         profile.setFirstName(request.getFirstName());
        if (request.getLastName() != null)          profile.setLastName(request.getLastName());
        if (request.getPhone() != null)             profile.setPhone(request.getPhone());
        if (request.getCity() != null)              profile.setCity(request.getCity());
        if (request.getHeadline() != null)          profile.setHeadline(request.getHeadline());
        if (request.getSummary() != null)           profile.setSummary(request.getSummary());
        if (request.getEmploymentStatus() != null)  profile.setEmploymentStatus(request.getEmploymentStatus());
        if (request.getNoticePeriod() != null)      profile.setNoticePeriod(request.getNoticePeriod());
        if (request.getExpectedSalaryMin() != null) profile.setExpectedSalaryMin(request.getExpectedSalaryMin());
        if (request.getExpectedSalaryMax() != null) profile.setExpectedSalaryMax(request.getExpectedSalaryMax());
        if (request.getPreferredJobType() != null)  profile.setPreferredJobType(request.getPreferredJobType());
        if (request.getLinkedinUrl() != null)       profile.setLinkedinUrl(request.getLinkedinUrl());
        if (request.getGithubUrl() != null)         profile.setGithubUrl(request.getGithubUrl());
        if (request.getPortfolioUrl() != null)      profile.setPortfolioUrl(request.getPortfolioUrl());

        profile.setProfileScore(calculateScore(profile));
        return CandidateProfileDto.from(profileRepository.save(profile));
    }

    // ── Work Experience ───────────────────────────────────────────────────────

    @Transactional
    public CandidateProfileDto addWorkExperience(UUID userId, WorkExperienceRequest request) {
        CandidateProfile profile = findProfileByUserId(userId);
        WorkExperience exp = WorkExperience.builder()
                .candidateProfile(profile)
                .companyName(request.getCompanyName())
                .jobTitle(request.getJobTitle())
                .employmentType(request.getEmploymentType())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .isCurrent(request.isCurrent())
                .description(request.getDescription())
                .build();
        profile.getWorkExperiences().add(exp);
        profile.setProfileScore(calculateScore(profile));
        return CandidateProfileDto.from(profileRepository.save(profile));
    }

    @Transactional
    public CandidateProfileDto updateWorkExperience(UUID userId, Long expId, WorkExperienceRequest request) {
        CandidateProfile profile = findProfileByUserId(userId);
        WorkExperience exp = workExperienceRepository
                .findByIdAndCandidateProfileId(expId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Work experience not found"));

        exp.setCompanyName(request.getCompanyName());
        exp.setJobTitle(request.getJobTitle());
        exp.setEmploymentType(request.getEmploymentType());
        exp.setStartDate(request.getStartDate());
        exp.setEndDate(request.getEndDate());
        exp.setCurrent(request.isCurrent());
        exp.setDescription(request.getDescription());
        workExperienceRepository.save(exp);
        return CandidateProfileDto.from(profileRepository.save(profile));
    }

    @Transactional
    public void deleteWorkExperience(UUID userId, Long expId) {
        CandidateProfile profile = findProfileByUserId(userId);
        WorkExperience exp = workExperienceRepository
                .findByIdAndCandidateProfileId(expId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Work experience not found"));
        profile.getWorkExperiences().remove(exp);
        profile.setProfileScore(calculateScore(profile));
        profileRepository.save(profile);
    }

    // ── Education ─────────────────────────────────────────────────────────────

    @Transactional
    public CandidateProfileDto addEducation(UUID userId, EducationRequest request) {
        CandidateProfile profile = findProfileByUserId(userId);
        Education edu = Education.builder()
                .candidateProfile(profile)
                .institutionName(request.getInstitutionName())
                .degree(request.getDegree())
                .fieldOfStudy(request.getFieldOfStudy())
                .startYear(request.getStartYear())
                .endYear(request.getEndYear())
                .isCurrent(request.isCurrent())
                .grade(request.getGrade())
                .build();
        profile.getEducations().add(edu);
        profile.setProfileScore(calculateScore(profile));
        return CandidateProfileDto.from(profileRepository.save(profile));
    }

    @Transactional
    public CandidateProfileDto updateEducation(UUID userId, Long eduId, EducationRequest request) {
        CandidateProfile profile = findProfileByUserId(userId);
        Education edu = educationRepository
                .findByIdAndCandidateProfileId(eduId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Education not found"));

        edu.setInstitutionName(request.getInstitutionName());
        edu.setDegree(request.getDegree());
        edu.setFieldOfStudy(request.getFieldOfStudy());
        edu.setStartYear(request.getStartYear());
        edu.setEndYear(request.getEndYear());
        edu.setCurrent(request.isCurrent());
        edu.setGrade(request.getGrade());
        educationRepository.save(edu);
        return CandidateProfileDto.from(profileRepository.save(profile));
    }

    @Transactional
    public void deleteEducation(UUID userId, Long eduId) {
        CandidateProfile profile = findProfileByUserId(userId);
        Education edu = educationRepository
                .findByIdAndCandidateProfileId(eduId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Education not found"));
        profile.getEducations().remove(edu);
        profile.setProfileScore(calculateScore(profile));
        profileRepository.save(profile);
    }

    // ── Skills ────────────────────────────────────────────────────────────────

    @Transactional
    public CandidateProfileDto addSkill(UUID userId, AddSkillRequest request) {
        CandidateProfile profile = findProfileByUserId(userId);
        if (skillRepository.existsByCandidateProfileIdAndSkillName(profile.getId(), request.getSkillName())) {
            throw new ConflictException("Skill already added: " + request.getSkillName());
        }
        CandidateSkill skill = CandidateSkill.builder()
                .candidateProfile(profile)
                .skillName(request.getSkillName())
                .proficiency(request.getProficiency())
                .yearsOfExperience(request.getYearsOfExperience())
                .build();
        profile.getSkills().add(skill);
        profile.setProfileScore(calculateScore(profile));
        return CandidateProfileDto.from(profileRepository.save(profile));
    }

    @Transactional
    public void deleteSkill(UUID userId, Long skillId) {
        CandidateProfile profile = findProfileByUserId(userId);
        CandidateSkill skill = skillRepository
                .findByIdAndCandidateProfileId(skillId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found"));
        profile.getSkills().remove(skill);
        profile.setProfileScore(calculateScore(profile));
        profileRepository.save(profile);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private CandidateProfile findProfileByUserId(UUID userId) {
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate profile not found"));
    }

    /**
     * Calculates profile completeness score out of 100.
     * Used to nudge candidates to fill in missing sections.
     */
    private int calculateScore(CandidateProfile p) {
        int score = 0;
        if (p.getFirstName() != null && p.getLastName() != null
                && p.getPhone() != null && p.getCity() != null)   score += 20;
        if (p.getHeadline() != null && p.getSummary() != null)    score += 15;
        if (!p.getWorkExperiences().isEmpty())                     score += 20;
        if (!p.getEducations().isEmpty())                          score += 15;
        if (p.getSkills().size() >= 3)                             score += 15;
        if (p.getLinkedinUrl() != null || p.getGithubUrl() != null) score += 5;
        return Math.min(score, 100);
    }
}
