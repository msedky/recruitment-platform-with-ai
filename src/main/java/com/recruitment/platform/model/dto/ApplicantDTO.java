package com.recruitment.platform.model.dto;

import com.recruitment.platform.common.dto.BaseDTO;
import com.recruitment.platform.model.enums.ApplicantStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ApplicantDTO extends BaseDTO {

    private String fullName;
    private String email;
    private String phone;
    private String nationality;
    private String address;
    private String linkedInUrl;
    private String portfolioUrl;
    private String summary;
    private ApplicantStatus status;
    private CvFileDTO cvFile;

    private Set<WorkExperienceDTO> workExperiences = new HashSet<>();
    private Set<SkillDTO> skills = new HashSet<>();
    private Set<CertificateDTO> certificates = new HashSet<>();
    private Set<EducationDTO> educations = new HashSet<>();
    private Set<LanguageDTO> languages = new HashSet<>();
}