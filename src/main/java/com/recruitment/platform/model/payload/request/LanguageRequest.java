package com.recruitment.platform.model.payload.request;

import com.recruitment.platform.model.enums.LanguageProficiency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LanguageRequest {

    @NotBlank(message = "Language name is required")
    private String name;

    @NotNull(message = "Proficiency is required")
    private LanguageProficiency proficiency;
}