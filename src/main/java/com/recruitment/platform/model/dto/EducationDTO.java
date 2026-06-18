package com.recruitment.platform.model.dto;

import com.recruitment.platform.common.dto.BaseDTO;
import com.recruitment.platform.model.enums.DegreeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EducationDTO extends BaseDTO {

    private String institution;
    private DegreeType degreeType;
    private String fieldOfStudy;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isCurrent;
    private String grade;
}