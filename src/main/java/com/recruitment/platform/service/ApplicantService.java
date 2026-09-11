package com.recruitment.platform.service;

import com.recruitment.platform.model.dto.ApplicantDTO;
import com.recruitment.platform.model.payload.request.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ApplicantService {

    ApplicantDTO uploadCvAndApply(MultipartFile file, UUID jobVacancyId);

    ApplicantDTO findById(UUID id);

    List<ApplicantDTO> findAll();

    ApplicantDTO updateBasicInfo(UUID id, ApplicantRequest request);

    ApplicantDTO updateSkills(UUID id, Set<SkillRequest> request);

    ApplicantDTO updateWorkExperiences(UUID id, Set<WorkExperienceRequest> request);

    ApplicantDTO updateEducations(UUID id, Set<EducationRequest> request);

    ApplicantDTO updateCertificates(UUID id, Set<CertificateRequest> request);

    ApplicantDTO updateLanguages(UUID id, Set<LanguageRequest> request);

    void delete(UUID id);
}