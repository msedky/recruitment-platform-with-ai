package com.recruitment.platform.service.impl;

import com.recruitment.platform.exception.InvalidCvFileException;
import com.recruitment.platform.mapper.*;
import com.recruitment.platform.model.dto.ApplicantDTO;
import com.recruitment.platform.model.entity.*;
import com.recruitment.platform.model.enums.ApplicantStatus;
import com.recruitment.platform.model.enums.ApplicationStatus;
import com.recruitment.platform.model.payload.request.*;
import com.recruitment.platform.repository.*;
import com.recruitment.platform.service.AiExtractionService;
import com.recruitment.platform.service.ApplicantService;
import com.recruitment.platform.service.CvParserService;
import com.recruitment.platform.service.CvStorageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
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
public class ApplicantServiceImpl implements ApplicantService {

    private final ApplicantRepository applicantRepository;
    private final SkillRepository skillRepository;
    private final WorkExperienceRepository workExperienceRepository;
    private final EducationRepository educationRepository;
    private final CertificateRepository certificateRepository;
    private final LanguageRepository languageRepository;

    private final ApplicantMapper applicantMapper;
    private final SkillMapper skillMapper;
    private final WorkExperienceMapper workExperienceMapper;
    private final EducationMapper educationMapper;
    private final CertificateMapper certificateMapper;
    private final LanguageMapper languageMapper;

    private final CvStorageService cvStorageService;
    private final CvFileRepository cvFileRepository;
    private final JobVacancyRepository jobVacancyRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final CvParserService cvParserService;
    private final AiExtractionService aiExtractionService;

    @Override
    public ApplicantDTO uploadCvAndApply(MultipartFile file, UUID jobVacancyId) {

        //Step 1: Save Job Application Data
        JobApplicationEntity jobApplication = saveJobApplication(file, jobVacancyId);

        //Step 2: Extract CV text from the uploaded file
        Resource fileResource = cvStorageService.load(jobApplication.getApplicant().getCvFile().getFilePath());
        String cvText = cvParserService.extractText(fileResource, jobApplication.getApplicant().getCvFile().getFileType());
        log.info("CV text extracted — applicantId: {}, length: {}", jobApplication.getApplicant().getId(), cvText.length());

        if (cvText == null || cvText.isBlank() || cvText.trim().length() < 50) {
            throw new InvalidCvFileException(
                    "Could not extract readable text from the uploaded CV. The file may be image-based, use unsupported fonts, or have a non-standard layout.");
        }
        log.info("cvText:\n{}", cvText);

        //Step 3: Extract Data from CV extracted text using AI Extraction Service
        ApplicantDTO dto = aiExtractionService.extractApplicantData(cvText);
        log.info("AI extraction completed — applicantId: {}", jobApplication.getApplicant().getId());

        //Step 4: Update Applicant Entity with extracted data
        saveJobApplicationAfterAiExtraction(jobApplication, dto);

        log.info("Extracted data persisted to DB — applicantId: {}", jobApplication.getApplicant().getId());
        dto = applicantMapper.toDTO(jobApplication.getApplicant());
        return dto;
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public ApplicantDTO findById(UUID id) {
        return applicantRepository.findById(id)
                .map(applicantMapper::toDTO).orElseThrow(() -> new EntityNotFoundException("Applicant not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicantDTO> findAll() {
        return applicantRepository.findAll()
                .stream()
                .map(applicantMapper::toDTO)
                .toList();
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

    private void validateFileType(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidCvFileException("Uploaded file is empty");
        }
        String contentType = file.getContentType();
        if (contentType == null ||
                (!contentType.equals("application/pdf") &&
                        !contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))) {
            throw new InvalidCvFileException(
                    "Unsupported file type: " + contentType + ". Only PDF and DOCX are accepted.");
        }
    }

    private void tryDeleteFile(String filePath) {
        try {
            cvStorageService.delete(filePath);
        } catch (Exception ex) {
            log.error("Compensating delete failed for path: {} — manual cleanup required",
                    filePath, ex);
        }
    }

    @Transactional
    public JobApplicationEntity saveJobApplication(MultipartFile file, UUID jobVacancyId) {
        validateFileType(file);

        JobVacancyEntity jobVacancy = jobVacancyRepository.findById(jobVacancyId).orElseThrow(() -> new EntityNotFoundException("Job vacancy not found with id: " + jobVacancyId));

        ApplicantEntity applicant = applicantRepository.save(ApplicantEntity.builder()
                .status(ApplicantStatus.UPLOAD_RECEIVED)
                .fullName("PENDING_EXTRACTION")
                .email("PENDING_" + UUID.randomUUID() + "@extraction.local")
                .build());

        String filePath = null;
        try {
            filePath = cvStorageService.store(file, applicant.getId());
            log.info("CV file stored at: {} for applicantId: {}", filePath, applicant.getId());

            CvFileEntity cvFile = cvFileRepository.save(CvFileEntity.builder()
                    .applicant(applicant)
                    .originalFileName(file.getOriginalFilename())
                    .storedFileName(applicant.getId() + "_" + file.getOriginalFilename())
                    .filePath(filePath)
                    .fileSize(file.getSize())
                    .fileType(file.getContentType())
                    .build());
            applicant.setCvFile(cvFile);
            applicant = applicantRepository.save(applicant);
        } catch (Exception e) {
            // Compensating action — delete the file if DB commit fails
            if (filePath != null) {
                log.warn("DB transaction failed — deleting stored file: {}", filePath);
                tryDeleteFile(filePath);
            }
            throw e;
        }

        return jobApplicationRepository.save(JobApplicationEntity.builder()
                .applicant(applicant)
                .jobVacancy(jobVacancy)
                .status(ApplicationStatus.APPLIED)
                .build());
    }

    @Transactional
    public void saveJobApplicationAfterAiExtraction(JobApplicationEntity jobApplication, ApplicantDTO dto) {
        applicantMapper.updateEntityFromDTO(dto, jobApplication.getApplicant());
        persistChildren(jobApplication.getApplicant(), dto);

        jobApplication.getApplicant().setStatus(ApplicantStatus.EXTRACTION_DONE);
        jobApplicationRepository.save(jobApplication);
        applicantRepository.save(jobApplication.getApplicant());
    }

    private void persistChildren(ApplicantEntity applicant, ApplicantDTO dto) {
        if (dto.getSkills() != null) {
            Set<SkillEntity> skills = dto.getSkills().stream()
                    .map(skillMapper::toEntity)
                    .peek(e -> e.setApplicant(applicant))
                    .collect(Collectors.toSet());
            skillRepository.saveAll(skills);
            applicant.setSkills(skills);
        }

        if (dto.getWorkExperiences() != null) {
            Set<WorkExperienceEntity> experiences = dto.getWorkExperiences().stream()
                    .map(workExperienceMapper::toEntity)
                    .peek(e -> e.setApplicant(applicant))
                    .collect(Collectors.toSet());
            workExperienceRepository.saveAll(experiences);
            applicant.setWorkExperiences(experiences);
        }

        if (dto.getEducations() != null) {
            Set<EducationEntity> educations = dto.getEducations().stream()
                    .map(educationMapper::toEntity)
                    .peek(e -> e.setApplicant(applicant))
                    .collect(Collectors.toSet());
            educationRepository.saveAll(educations);
            applicant.setEducations(educations);
        }

        if (dto.getCertificates() != null) {
            Set<CertificateEntity> certificates = dto.getCertificates().stream()
                    .map(certificateMapper::toEntity)
                    .peek(e -> e.setApplicant(applicant))
                    .collect(Collectors.toSet());
            certificateRepository.saveAll(certificates);
            applicant.setCertificates(certificates);
        }

        if (dto.getLanguages() != null) {
            Set<LanguageEntity> languages = dto.getLanguages().stream()
                    .map(languageMapper::toEntity)
                    .peek(e -> e.setApplicant(applicant))
                    .collect(Collectors.toSet());
            languageRepository.saveAll(languages);
            applicant.setLanguages(languages);
        }
    }
}