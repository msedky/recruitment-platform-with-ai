package com.recruitment.platform.model.dto;

import com.recruitment.platform.common.dto.BaseDTO;
import com.recruitment.platform.model.enums.LanguageProficiency;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class LanguageDTO extends BaseDTO {

    private String name;
    private LanguageProficiency proficiency;
}