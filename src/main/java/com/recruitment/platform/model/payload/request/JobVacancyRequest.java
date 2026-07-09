package com.recruitment.platform.model.payload.request;

import com.recruitment.platform.model.dto.JobRequiredSkillDTO;
import com.recruitment.platform.model.enums.DegreeType;
import com.recruitment.platform.model.enums.EmploymentType;
import com.recruitment.platform.model.enums.WorkModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class JobVacancyRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String description;

    private String department;
    private String location;

    @NotNull
    private EmploymentType employmentType;

    @NotNull
    private WorkModel workModel;

    private Integer minExperienceYears;
    private DegreeType requiredDegreeType;
    private String requiredFieldOfStudy;

    @Builder.Default
    private Set<JobRequiredSkillDTO> requiredSkills = new HashSet<>();

    private LocalDate postedDate;
    private LocalDate closingDate;
}
