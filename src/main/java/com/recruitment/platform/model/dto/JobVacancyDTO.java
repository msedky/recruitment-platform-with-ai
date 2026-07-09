package com.recruitment.platform.model.dto;

import com.recruitment.platform.common.dto.BaseDTO;
import com.recruitment.platform.model.enums.DegreeType;
import com.recruitment.platform.model.enums.EmploymentType;
import com.recruitment.platform.model.enums.VacancyStatus;
import com.recruitment.platform.model.enums.WorkModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class JobVacancyDTO extends BaseDTO {

    private String title;
    private String description;
    private String department;
    private String location;
    private EmploymentType employmentType;
    private WorkModel workModel;
    private Integer minExperienceYears;
    private DegreeType requiredDegreeType;
    private String requiredFieldOfStudy;
    private Set<JobRequiredSkillDTO> requiredSkills;
    private VacancyStatus status;
    private LocalDate postedDate;
    private LocalDate closingDate;
}
