package com.recruitment.platform.service;

import com.recruitment.platform.model.dto.ApplicantDTO;
import com.recruitment.platform.model.payload.request.ApplicantRequest;
import com.recruitment.platform.model.payload.request.CertificateRequest;
import com.recruitment.platform.model.payload.request.EducationRequest;
import com.recruitment.platform.model.payload.request.LanguageRequest;
import com.recruitment.platform.model.payload.request.SkillRequest;
import com.recruitment.platform.model.payload.request.WorkExperienceRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ApplicantService {

    /**
     * Phase 1 — stores the CV file, persists skeleton ApplicantEntity,
     * creates OutboxEntity for async processing.
     * Returns applicantId immediately — AI extraction happens asynchronously.
     */
    UUID uploadAndExtract(MultipartFile file);

    /**
     * Finds applicant by ID.
     * Fast path: queries ES first (single document, no joins).
     * Fallback: queries PostgreSQL if ES miss or ES is down.
     */
    ApplicantDTO findById(UUID id);

    List<ApplicantDTO> findAll();

    /**
     * Full-text keyword search across name, summary, skills, job titles, companies.
     * ES only — no PostgreSQL fallback.
     * Returns 503 if ES is unavailable.
     */
    List<ApplicantDTO> search(String keyword);

    ApplicantDTO updateBasicInfo(UUID id, ApplicantRequest request);

    ApplicantDTO updateSkills(UUID id, Set<SkillRequest> request);

    ApplicantDTO updateWorkExperiences(UUID id, Set<WorkExperienceRequest> request);

    ApplicantDTO updateEducations(UUID id, Set<EducationRequest> request);

    ApplicantDTO updateCertificates(UUID id, Set<CertificateRequest> request);

    ApplicantDTO updateLanguages(UUID id, Set<LanguageRequest> request);

    void delete(UUID id);
}