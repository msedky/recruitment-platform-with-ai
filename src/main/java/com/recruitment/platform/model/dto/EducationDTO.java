package com.recruitment.platform.model.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.recruitment.platform.common.dto.BaseDTO;
import com.recruitment.platform.model.enums.DegreeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EducationDTO extends BaseDTO {

    private String institution;
    private DegreeType degreeType;
    @JsonAlias({"field_of_study", "major"})
    private String fieldOfStudy;
    private LocalDate startDate;
    private LocalDate endDate;
    @JsonAlias({"current"})
    private Boolean isCurrent;
    private String grade;
}