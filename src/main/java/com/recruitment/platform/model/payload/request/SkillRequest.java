package com.recruitment.platform.model.payload.request;

import com.recruitment.platform.model.enums.SkillLevel;
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
public class SkillRequest {

    @NotBlank(message = "Skill name is required")
    private String name;

    @NotNull(message = "Skill level is required")
    private SkillLevel level;
}