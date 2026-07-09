package com.recruitment.platform.model.entity;

import com.recruitment.platform.common.entity.BaseEntity;
import com.recruitment.platform.model.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Entity
@Table(name = "job_applications",
        uniqueConstraints = @UniqueConstraint(columnNames = {"applicant_id", "job_vacancy_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class JobApplicationEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    private ApplicantEntity applicant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_vacancy_id", nullable = false)
    private JobVacancyEntity jobVacancy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    @Column
    private Double matchScore;

    @Column(columnDefinition = "TEXT")
    private String aiEvaluationSummary;

    // populated later, during the holistic shortlisting run — comparative, not absolute
    @Column
    private Integer shortlistRank;

    @Column(columnDefinition = "TEXT")
    private String shortlistJustification;

    @Column
    private Instant shortlistedAt;
}