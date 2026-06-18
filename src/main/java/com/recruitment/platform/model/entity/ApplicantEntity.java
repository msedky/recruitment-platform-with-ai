package com.recruitment.platform.model.entity;

import com.recruitment.platform.common.entity.BaseEntity;
import com.recruitment.platform.model.enums.ApplicantStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "applicants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ApplicantEntity extends BaseEntity {

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String phone;

    @Column
    private String nationality;

    @Column
    private String address;

    @Column
    private String linkedInUrl;

    @Column
    private String portfolioUrl;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicantStatus status;

    @OneToOne(mappedBy = "applicant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CvFileEntity cvFile;

    @OneToMany(mappedBy = "applicant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<WorkExperienceEntity> workExperiences = new HashSet<>();

    @OneToMany(mappedBy = "applicant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<EducationEntity> educations = new HashSet<>();

    @OneToMany(mappedBy = "applicant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<SkillEntity> skills = new HashSet<>();

    @OneToMany(mappedBy = "applicant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<CertificateEntity> certificates = new HashSet<>();

    @OneToMany(mappedBy = "applicant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<LanguageEntity> languages = new HashSet<>();
}