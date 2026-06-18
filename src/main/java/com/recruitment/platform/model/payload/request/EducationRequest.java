package com.recruitment.platform.model.payload.request;

import com.recruitment.platform.model.enums.DegreeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EducationRequest {

    @NotBlank(message = "Institution is required")
    private String institution;

    @NotNull(message = "Degree type is required")
    private DegreeType degreeType;

    @NotBlank(message = "Field of study is required")
    private String fieldOfStudy;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    private LocalDate endDate;

    private boolean isCurrent;

    private String grade;
}