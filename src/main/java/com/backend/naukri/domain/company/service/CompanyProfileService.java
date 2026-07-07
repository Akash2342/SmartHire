package com.backend.naukri.domain.company.service;

import com.backend.naukri.domain.company.dto.CompanyProfileDto;
import com.backend.naukri.domain.company.dto.CreateCompanyRequest;
import com.backend.naukri.domain.company.dto.UpdateCompanyRequest;
import com.backend.naukri.domain.company.entity.CompanyProfile;
import com.backend.naukri.domain.company.repository.CompanyProfileRepository;
import com.backend.naukri.domain.user.entity.User;
import com.backend.naukri.domain.user.repository.UserRepository;
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
 * Manages company profile creation, updates, and logo upload.
 * A recruiter can only have one company profile.
 * Verification status is managed by admin only.
 */
@Service
@RequiredArgsConstructor
public class CompanyProfileService {

    private static final List<String> ALLOWED_IMAGE_TYPES = List.of(
            "image/jpeg", "image/png", "image/webp"
    );
    private static final long MAX_LOGO_SIZE = 2 * 1024 * 1024; // 2MB

    private final CompanyProfileRepository companyProfileRepository;
    private final UserRepository userRepository;

    /** Creates a new company profile for the recruiter. Only one allowed. */
    @Transactional
    public CompanyProfileDto create(UUID userId, CreateCompanyRequest request) {
        if (companyProfileRepository.existsByUserId(userId)) {
            throw new ConflictException("Company profile already exists. Use update to modify it.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        CompanyProfile company = CompanyProfile.builder()
                .user(user)
                .companyName(request.getCompanyName())
                .displayName(request.getDisplayName())
                .industry(request.getIndustry())
                .companySize(request.getCompanySize())
                .foundedYear(request.getFoundedYear())
                .websiteUrl(request.getWebsiteUrl())
                .description(request.getDescription())
                .headquartersCity(request.getHeadquartersCity())
                .build();

        return CompanyProfileDto.from(companyProfileRepository.save(company));
    }

    /** Returns the recruiter's own company profile. */
    @Transactional(readOnly = true)
    public CompanyProfileDto getMyProfile(UUID userId) {
        return CompanyProfileDto.from(findByUserId(userId));
    }

    /** Returns any company profile by ID — used in public job listings. */
    @Transactional(readOnly = true)
    public CompanyProfileDto getById(UUID companyId) {
        CompanyProfile company = companyProfileRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        return CompanyProfileDto.from(company);
    }

    /** Partial update — only non-null fields are applied. */
    @Transactional
    public CompanyProfileDto update(UUID userId, UpdateCompanyRequest request) {
        CompanyProfile company = findByUserId(userId);

        if (request.getCompanyName() != null)      company.setCompanyName(request.getCompanyName());
        if (request.getDisplayName() != null)      company.setDisplayName(request.getDisplayName());
        if (request.getIndustry() != null)         company.setIndustry(request.getIndustry());
        if (request.getCompanySize() != null)      company.setCompanySize(request.getCompanySize());
        if (request.getFoundedYear() != null)      company.setFoundedYear(request.getFoundedYear());
        if (request.getWebsiteUrl() != null)       company.setWebsiteUrl(request.getWebsiteUrl());
        if (request.getDescription() != null)      company.setDescription(request.getDescription());
        if (request.getHeadquartersCity() != null) company.setHeadquartersCity(request.getHeadquartersCity());

        return CompanyProfileDto.from(companyProfileRepository.save(company));
    }

    /** Uploads or replaces the company logo. Accepts JPEG, PNG, WEBP up to 2MB. */
    @Transactional
    public CompanyProfileDto uploadLogo(UUID userId, MultipartFile file) throws IOException {
        CompanyProfile company = findByUserId(userId);

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new BadRequestException("Only JPEG, PNG, and WEBP images are allowed.");
        }
        if (file.getSize() > MAX_LOGO_SIZE) {
            throw new BadRequestException("Logo size must not exceed 2MB.");
        }

        company.setLogo(file.getBytes());
        company.setLogoContentType(contentType);

        return CompanyProfileDto.from(companyProfileRepository.save(company));
    }

    /** Streams the logo bytes for display. */
    @Transactional(readOnly = true)
    public CompanyProfile getCompanyWithLogo(UUID companyId) {
        CompanyProfile company = companyProfileRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        if (company.getLogo() == null) {
            throw new ResourceNotFoundException("No logo uploaded for this company.");
        }
        return company;
    }

    private CompanyProfile findByUserId(UUID userId) {
        return companyProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Company profile not found. Please create one first."));
    }
}
