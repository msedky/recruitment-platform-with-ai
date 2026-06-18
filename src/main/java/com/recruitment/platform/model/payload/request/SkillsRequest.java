package com.recruitment.platform.model.payload.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillsRequest {

    @Valid
    @NotEmpty(message = "Skills list cannot be empty")
    private Set<SkillRequest> skills;
}