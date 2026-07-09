package com.recruitment.platform.model.entity;

import com.recruitment.platform.common.entity.BaseEntity;
import com.recruitment.platform.model.enums.SkillLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "job_required_skills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class JobRequiredSkillEntity extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SkillLevel minimumLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_vacancy_id", nullable = false)
    private JobVacancyEntity jobVacancy;
}