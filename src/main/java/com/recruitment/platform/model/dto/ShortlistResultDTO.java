package com.recruitment.platform.model.dto;

import com.recruitment.platform.common.dto.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ShortlistResultDTO extends BaseDTO {
    private UUID applicantId;
    private String applicantFullName;
    private int rank;
    private Double matchScore;
    private String justification;
}
