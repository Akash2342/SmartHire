package com.backend.naukri.domain.candidate.dto;

import com.backend.naukri.common.enums.EmploymentType;
import com.backend.naukri.common.enums.NoticePeriod;
import com.backend.naukri.common.enums.ProficiencyLevel;
import com.backend.naukri.domain.candidate.entity.CandidateProfile;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class CandidateProfileDto {

    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String city;
    private String headline;
    private String summary;
    private String employmentStatus;
    private NoticePeriod noticePeriod;
    private Integer expectedSalaryMin;
    private Integer expectedSalaryMax;
    private String salaryCurrency;
    private EmploymentType preferredJobType;
    private String linkedinUrl;
    private String githubUrl;
    private String portfolioUrl;
    private Integer profileScore;
    private List<WorkExperienceDto> workExperiences;
    private List<EducationDto> educations;
    private List<SkillDto> skills;
    private LocalDateTime createdAt;

    public static CandidateProfileDto from(CandidateProfile p) {
        CandidateProfileDto dto = new CandidateProfileDto();
        dto.setId(p.getId());
        dto.setEmail(p.getUser().getEmail());
        dto.setFirstName(p.getFirstName());
        dto.setLastName(p.getLastName());
        dto.setPhone(p.getPhone());
        dto.setCity(p.getCity());
        dto.setHeadline(p.getHeadline());
        dto.setSummary(p.getSummary());
        dto.setEmploymentStatus(p.getEmploymentStatus());
        dto.setNoticePeriod(p.getNoticePeriod());
        dto.setExpectedSalaryMin(p.getExpectedSalaryMin());
        dto.setExpectedSalaryMax(p.getExpectedSalaryMax());
        dto.setSalaryCurrency(p.getSalaryCurrency());
        dto.setPreferredJobType(p.getPreferredJobType());
        dto.setLinkedinUrl(p.getLinkedinUrl());
        dto.setGithubUrl(p.getGithubUrl());
        dto.setPortfolioUrl(p.getPortfolioUrl());
        dto.setProfileScore(p.getProfileScore());
        dto.setCreatedAt(p.getCreatedAt());

        dto.setWorkExperiences(p.getWorkExperiences().stream()
                .map(WorkExperienceDto::from).toList());
        dto.setEducations(p.getEducations().stream()
                .map(EducationDto::from).toList());
        dto.setSkills(p.getSkills().stream()
                .map(SkillDto::from).toList());
        return dto;
    }

    @Data
    public static class WorkExperienceDto {
        private Long id;
        private String companyName;
        private String jobTitle;
        private EmploymentType employmentType;
        private LocalDate startDate;
        private LocalDate endDate;
        private boolean isCurrent;
        private String description;

        public static WorkExperienceDto from(com.backend.naukri.domain.candidate.entity.WorkExperience e) {
            WorkExperienceDto dto = new WorkExperienceDto();
            dto.setId(e.getId());
            dto.setCompanyName(e.getCompanyName());
            dto.setJobTitle(e.getJobTitle());
            dto.setEmploymentType(e.getEmploymentType());
            dto.setStartDate(e.getStartDate());
            dto.setEndDate(e.getEndDate());
            dto.setCurrent(e.isCurrent());
            dto.setDescription(e.getDescription());
            return dto;
        }
    }

    @Data
    public static class EducationDto {
        private Long id;
        private String institutionName;
        private String degree;
        private String fieldOfStudy;
        private Integer startYear;
        private Integer endYear;
        private boolean isCurrent;
        private String grade;

        public static EducationDto from(com.backend.naukri.domain.candidate.entity.Education e) {
            EducationDto dto = new EducationDto();
            dto.setId(e.getId());
            dto.setInstitutionName(e.getInstitutionName());
            dto.setDegree(e.getDegree());
            dto.setFieldOfStudy(e.getFieldOfStudy());
            dto.setStartYear(e.getStartYear());
            dto.setEndYear(e.getEndYear());
            dto.setCurrent(e.isCurrent());
            dto.setGrade(e.getGrade());
            return dto;
        }
    }

    @Data
    public static class SkillDto {
        private Long id;
        private String skillName;
        private ProficiencyLevel proficiency;
        private Integer yearsOfExperience;

        public static SkillDto from(com.backend.naukri.domain.candidate.entity.CandidateSkill s) {
            SkillDto dto = new SkillDto();
            dto.setId(s.getId());
            dto.setSkillName(s.getSkillName());
            dto.setProficiency(s.getProficiency());
            dto.setYearsOfExperience(s.getYearsOfExperience());
            return dto;
        }
    }
}
