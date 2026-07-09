package com.recruitment.platform.model.entity;

import com.recruitment.platform.common.entity.BaseEntity;
import com.recruitment.platform.model.enums.DegreeType;
import com.recruitment.platform.model.enums.EmploymentType;
import com.recruitment.platform.model.enums.VacancyStatus;
import com.recruitment.platform.model.enums.WorkModel;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "job_vacancies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class JobVacancyEntity extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column
    private String department;

    @Column
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmploymentType employmentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkModel workModel;

    @Column
    private Integer minExperienceYears;

    @Enumerated(EnumType.STRING)
    @Column
    private DegreeType requiredDegreeType;

    @Column
    private String requiredFieldOfStudy;

    @OneToMany(mappedBy = "jobVacancy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<JobRequiredSkillEntity> requiredSkills = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VacancyStatus status;

    @Column
    private LocalDate postedDate;

    @Column
    private LocalDate closingDate;

    @OneToMany(mappedBy = "jobVacancy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<JobApplicationEntity> applications = new HashSet<>();
}