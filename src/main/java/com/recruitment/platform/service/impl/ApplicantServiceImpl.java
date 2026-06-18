package com.recruitment.platform.service.impl;

import com.recruitment.platform.exception.SearchUnavailableException;
import com.recruitment.platform.mapper.ApplicantMapper;
import com.recruitment.platform.repository.ApplicantSearchRepository;
import com.recruitment.platform.model.dto.ApplicantDTO;
import com.recruitment.platform.model.entity.ApplicantEntity;
import com.recruitment.platform.model.entity.CertificateEntity;
import com.recruitment.platform.model.entity.EducationEntity;
import com.recruitment.platform.model.entity.LanguageEntity;
import com.recruitment.platform.model.entity.SkillEntity;
import com.recruitment.platform.model.entity.WorkExperienceEntity;
import com.recruitment.platform.model.payload.request.ApplicantRequest;
import com.recruitment.platform.model.payload.request.CertificateRequest;
import com.recruitment.platform.model.payload.request.EducationRequest;
import com.recruitment.platform.model.payload.request.LanguageRequest;
import com.recruitment.platform.model.payload.request.SkillRequest;
import com.recruitment.platform.model.payload.request.WorkExperienceRequest;
import com.recruitment.platform.repository.ApplicantRepository;
import com.recruitment.platform.service.ApplicantService;
import com.recruitment.platform.service.CvUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ApplicantServiceImpl implements ApplicantService {

    private final ApplicantRepository applicantRepository;
    private final ApplicantMapper applicantMapper;
    private final ApplicantSearchRepository applicantSearchRepository;
    private final CvUploadService cvUploadService;

    // ── Upload ────────────────────────────────────────────────────────────────

    /**
     * Delegates to CvUploadService which handles Phase 1 of the async flow:
     * store file → persist skeleton ApplicantEntity → persist OutboxEntity.
     * Returns applicantId immediately — AI extraction is async.
     */
    @Override
    public UUID uploadAndExtract(MultipartFile file) {
        return cvUploadService.handleUpload(file);
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public ApplicantDTO findById(UUID id) {
        // Fast path — query ES first (single document, no joins)
        try {
            return applicantSearchRepository.findById(id.toString())
                    .map(applicantMapper::toDTO)
                    .orElseGet(() -> {
                        log.info("ES miss for applicantId: {} — falling back to PostgreSQL", id);
                        return applicantMapper.toDTO(findApplicantById(id));
                    });
        } catch (Exception esException) {
            // ES is down — fall back to PostgreSQL
            log.warn("ES unavailable for findById: {} — falling back to PostgreSQL. Error: {}",
                    id, esException.getMessage());
            return applicantMapper.toDTO(findApplicantById(id));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicantDTO> search(String keyword) {
        // ES only — no PostgreSQL fallback for search
        try {
            return applicantSearchRepository
                    .findByFullNameContainingOrSummaryContainingOrSkillsContaining(
                            keyword, keyword, keyword)
                    .stream()
                    .map(applicantMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("ES unavailable for search — keyword: {}", keyword);
            throw new SearchUnavailableException(
                    "Search service is currently unavailable — please try again later", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicantDTO> findAll() {
        return applicantRepository.findAll()
                .stream()
                .map(applicantMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ── Updates ───────────────────────────────────────────────────────────────

    @Override
    public ApplicantDTO updateBasicInfo(UUID id, ApplicantRequest request) {
        ApplicantEntity entity = findApplicantById(id);
        entity.setFullName(request.getFullName());
        entity.setEmail(request.getEmail());
        entity.setPhone(request.getPhone());
        entity.setNationality(request.getNationality());
        entity.setAddress(request.getAddress());
        entity.setLinkedInUrl(request.getLinkedInUrl());
        entity.setPortfolioUrl(request.getPortfolioUrl());
        entity.setSummary(request.getSummary());
        ApplicantEntity updatedEntity = applicantRepository.save(entity);
        log.info("Applicant basic info updated with id: {}", id);
        return applicantMapper.toDTO(updatedEntity);
    }

    @Override
    public ApplicantDTO updateSkills(UUID id, Set<SkillRequest> request) {
        ApplicantEntity entity = findApplicantById(id);
        entity.getSkills().clear();
        Set<SkillEntity> newSkills = request.stream()
                .map(r -> SkillEntity.builder()
                        .name(r.getName())
                        .level(r.getLevel())
                        .applicant(entity)
                        .build())
                .collect(Collectors.toSet());
        entity.getSkills().addAll(newSkills);
        log.info("Applicant skills updated with id: {}", id);
        return applicantMapper.toDTO(applicantRepository.save(entity));
    }

    @Override
    public ApplicantDTO updateWorkExperiences(UUID id, Set<WorkExperienceRequest> request) {
        ApplicantEntity entity = findApplicantById(id);
        entity.getWorkExperiences().clear();
        Set<WorkExperienceEntity> newWorkExperiences = request.stream()
                .map(r -> WorkExperienceEntity.builder()
                        .companyName(r.getCompanyName())
                        .jobTitle(r.getJobTitle())
                        .roleType(r.getRoleType())
                        .startDate(r.getStartDate())
                        .endDate(r.getEndDate())
                        .isCurrent(r.isCurrent())
                        .description(r.getDescription())
                        .applicant(entity)
                        .build())
                .collect(Collectors.toSet());
        entity.getWorkExperiences().addAll(newWorkExperiences);
        log.info("Applicant work experiences updated with id: {}", id);
        return applicantMapper.toDTO(applicantRepository.save(entity));
    }

    @Override
    public ApplicantDTO updateEducations(UUID id, Set<EducationRequest> request) {
        ApplicantEntity entity = findApplicantById(id);
        entity.getEducations().clear();
        Set<EducationEntity> newEducations = request.stream()
                .map(r -> EducationEntity.builder()
                        .institution(r.getInstitution())
                        .degreeType(r.getDegreeType())
                        .fieldOfStudy(r.getFieldOfStudy())
                        .startDate(r.getStartDate())
                        .endDate(r.getEndDate())
                        .isCurrent(r.isCurrent())
                        .grade(r.getGrade())
                        .applicant(entity)
                        .build())
                .collect(Collectors.toSet());
        entity.getEducations().addAll(newEducations);
        log.info("Applicant educations updated with id: {}", id);
        return applicantMapper.toDTO(applicantRepository.save(entity));
    }

    @Override
    public ApplicantDTO updateCertificates(UUID id, Set<CertificateRequest> request) {
        ApplicantEntity entity = findApplicantById(id);
        entity.getCertificates().clear();
        Set<CertificateEntity> newCertificates = request.stream()
                .map(r -> CertificateEntity.builder()
                        .name(r.getName())
                        .issuingOrganization(r.getIssuingOrganization())
                        .issueDate(r.getIssueDate())
                        .expiryDate(r.getExpiryDate())
                        .applicant(entity)
                        .build())
                .collect(Collectors.toSet());
        entity.getCertificates().addAll(newCertificates);
        log.info("Applicant certificates updated with id: {}", id);
        return applicantMapper.toDTO(applicantRepository.save(entity));
    }

    @Override
    public ApplicantDTO updateLanguages(UUID id, Set<LanguageRequest> request) {
        ApplicantEntity entity = findApplicantById(id);
        entity.getLanguages().clear();
        Set<LanguageEntity> newLanguages = request.stream()
                .map(r -> LanguageEntity.builder()
                        .name(r.getName())
                        .proficiency(r.getProficiency())
                        .applicant(entity)
                        .build())
                .collect(Collectors.toSet());
        entity.getLanguages().addAll(newLanguages);
        log.info("Applicant languages updated with id: {}", id);
        return applicantMapper.toDTO(applicantRepository.save(entity));
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    @Override
    public void delete(UUID id) {
        ApplicantEntity entity = findApplicantById(id);
        applicantRepository.delete(entity);
        log.info("Applicant deleted with id: {}", id);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private ApplicantEntity findApplicantById(UUID id) {
        return applicantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Applicant not found with id: " + id));
    }
}