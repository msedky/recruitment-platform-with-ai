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
public class ShortlistCandidateDTO extends BaseDTO {

    private UUID applicantId;
    private int rank;
    private String justification;
}