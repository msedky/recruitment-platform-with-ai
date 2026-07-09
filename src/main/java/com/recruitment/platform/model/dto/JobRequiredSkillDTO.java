package com.recruitment.platform.model.dto;

import com.recruitment.platform.common.dto.BaseDTO;
import com.recruitment.platform.model.enums.SkillLevel;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class JobRequiredSkillDTO extends BaseDTO {
    private String name;
    private SkillLevel minimumLevel;
}