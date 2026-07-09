package com.recruitment.platform.model.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.recruitment.platform.common.dto.BaseDTO;
import com.recruitment.platform.model.enums.RoleType;
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
public class WorkExperienceDTO extends BaseDTO {

    @JsonAlias({"company"})
    private String companyName;
    @JsonAlias({"role", "title", "position"})
    private String jobTitle;
    private RoleType roleType;
    private LocalDate startDate;
    private LocalDate endDate;
    @JsonAlias({"current"})
    private Boolean isCurrent;
    private String description;
}