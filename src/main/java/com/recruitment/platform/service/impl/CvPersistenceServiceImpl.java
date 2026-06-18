package com.recruitment.platform.service.impl;

import com.recruitment.platform.mapper.*;
import com.recruitment.platform.model.dto.ApplicantDTO;
import com.recruitment.platform.model.entity.*;
import com.recruitment.platform.model.enums.ApplicantStatus;
import com.recruitment.platform.repository.*;
import com.recruitment.platform.service.CvPersistenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CvPersistenceServiceImpl implements CvPersistenceService {

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

    @Override
    @Transactional
    public void updateStatus(UUID applicantId, ApplicantStatus status) {
        applicantRepository.findById(applicantId).ifPresentOrElse(
                applicant -> {
                    applicant.setStatus(status);
                    applicantRepository.save(applicant);
                },
                () -> log.warn("Applicant not found for status update — id: {}", applicantId)
        );
    }

    /**
     * Persists the full extracted DTO to PostgreSQL in its own transaction.
     * Called AFTER the AI call completes — no DB connection is held during AI inference.
     * <p>
     * Transaction scope: opens here, commits at method exit.
     * If this fails → caller catches and rethrows → nack → retry → AI called again.
     * This is the only unavoidable re-call scenario.
     */
    @Override
    @Transactional
    public ApplicantEntity persistExtractedData(UUID applicantId, ApplicantDTO dto) {
        ApplicantEntity applicant = applicantRepository.findById(applicantId)
                .orElseThrow(() -> new RuntimeException(
                        "ApplicantEntity not found for id: " + applicantId));

        applicantMapper.updateEntityFromDTO(dto, applicant);
        persistChildren(applicant, dto);

        applicant.setStatus(ApplicantStatus.EXTRACTION_DONE);
        ApplicantEntity saved = applicantRepository.save(applicant);
        log.info("Extracted data persisted to DB — applicantId: {}", applicantId);
        return saved;
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void persistChildren(ApplicantEntity applicant, ApplicantDTO dto) {
        if (dto.getSkills() != null) {
            Set<SkillEntity> skills = dto.getSkills().stream()
                    .map(o -> {
                        SkillEntity entity = skillMapper.toEntity(o);
                        entity.setId(UUID.randomUUID());
                        return entity;
                    })
                    .peek(e -> e.setApplicant(applicant))
                    .collect(Collectors.toSet());
            skillRepository.saveAll(skills);
        }

        if (dto.getWorkExperiences() != null) {
            Set<WorkExperienceEntity> experiences = dto.getWorkExperiences().stream()
                    .map(o -> {
                        WorkExperienceEntity entity = workExperienceMapper.toEntity(o);
                        entity.setId(UUID.randomUUID());
                        return entity;
                    })
                    .peek(e -> e.setApplicant(applicant))
                    .collect(Collectors.toSet());
            workExperienceRepository.saveAll(experiences);
        }

        if (dto.getEducations() != null) {
            Set<EducationEntity> educations = dto.getEducations().stream()
                    .map(o -> {
                        EducationEntity entity = educationMapper.toEntity(o);
                        entity.setId(UUID.randomUUID());
                        return entity;
                    })
                    .peek(e -> e.setApplicant(applicant))
                    .collect(Collectors.toSet());
            educationRepository.saveAll(educations);
        }

        if (dto.getCertificates() != null) {
            Set<CertificateEntity> certificates = dto.getCertificates().stream()
                    .map(o -> {
                        CertificateEntity entity = certificateMapper.toEntity(o);
                        entity.setId(UUID.randomUUID());
                        return entity;
                    })
                    .peek(e -> e.setApplicant(applicant))
                    .collect(Collectors.toSet());
            certificateRepository.saveAll(certificates);
        }

        if (dto.getLanguages() != null) {
            Set<LanguageEntity> languages = dto.getLanguages().stream()
                    .map(o -> {
                        LanguageEntity entity = languageMapper.toEntity(o);
                        entity.setId(UUID.randomUUID());
                        return entity;
                    })
                    .peek(e -> e.setApplicant(applicant))
                    .collect(Collectors.toSet());
            languageRepository.saveAll(languages);
        }
    }
}