package com.recruitment.platform.controller;

import com.recruitment.platform.model.dto.ApplicantDTO;
import com.recruitment.platform.model.payload.request.ApplicantRequest;
import com.recruitment.platform.model.payload.request.CertificateRequest;
import com.recruitment.platform.model.payload.request.EducationRequest;
import com.recruitment.platform.model.payload.request.LanguageRequest;
import com.recruitment.platform.model.payload.request.SkillRequest;
import com.recruitment.platform.model.payload.request.WorkExperienceRequest;
import com.recruitment.platform.service.ApplicantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/applicants")
@RequiredArgsConstructor
public class ApplicantController {

    private final ApplicantService applicantService;

    @PostMapping(value = "/upload/{jobVacancyId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApplicantDTO> uploadCv(
            @RequestParam("file") MultipartFile file, @PathVariable UUID jobVacancyId) {
        log.info("Received CV upload request, file: {}", file.getOriginalFilename());
        ApplicantDTO applicant = applicantService.uploadAndExtract(file, jobVacancyId);
        return ResponseEntity.ok(applicant);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicantDTO> findById(@PathVariable UUID id) {
        log.info("Received request to find applicant by id: {}", id);
        return ResponseEntity.ok(applicantService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<ApplicantDTO>> findAll() {
        log.info("Received request to find all applicants");
        return ResponseEntity.ok(applicantService.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApplicantDTO> updateBasicInfo(
            @PathVariable UUID id,
            @RequestBody @Valid ApplicantRequest request) {
        log.info("Received request to update applicant basic info with id: {}", id);
        return ResponseEntity.ok(applicantService.updateBasicInfo(id, request));
    }

    @PutMapping("/{id}/skills")
    public ResponseEntity<ApplicantDTO> updateSkills(
            @PathVariable UUID id,
            @RequestBody @Valid Set<SkillRequest> request) {
        log.info("Received request to update skills for applicant id: {}", id);
        return ResponseEntity.ok(applicantService.updateSkills(id, request));
    }

    @PutMapping("/{id}/work-experiences")
    public ResponseEntity<ApplicantDTO> updateWorkExperiences(
            @PathVariable UUID id,
            @RequestBody @Valid Set<WorkExperienceRequest> request) {
        log.info("Received request to update work experiences for applicant id: {}", id);
        return ResponseEntity.ok(applicantService.updateWorkExperiences(id, request));
    }

    @PutMapping("/{id}/educations")
    public ResponseEntity<ApplicantDTO> updateEducations(
            @PathVariable UUID id,
            @RequestBody @Valid Set<EducationRequest> request) {
        log.info("Received request to update educations for applicant id: {}", id);
        return ResponseEntity.ok(applicantService.updateEducations(id, request));
    }

    @PutMapping("/{id}/certificates")
    public ResponseEntity<ApplicantDTO> updateCertificates(
            @PathVariable UUID id,
            @RequestBody @Valid Set<CertificateRequest> request) {
        log.info("Received request to update certificates for applicant id: {}", id);
        return ResponseEntity.ok(applicantService.updateCertificates(id, request));
    }

    @PutMapping("/{id}/languages")
    public ResponseEntity<ApplicantDTO> updateLanguages(
            @PathVariable UUID id,
            @RequestBody @Valid Set<LanguageRequest> request) {
        log.info("Received request to update languages for applicant id: {}", id);
        return ResponseEntity.ok(applicantService.updateLanguages(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("Received request to delete applicant with id: {}", id);
        applicantService.delete(id);
        return ResponseEntity.noContent().build();
    }
}