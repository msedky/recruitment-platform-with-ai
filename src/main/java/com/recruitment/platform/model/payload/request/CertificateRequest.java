package com.recruitment.platform.model.payload.request;

import jakarta.validation.constraints.NotBlank;
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
public class CertificateRequest {

    @NotBlank(message = "Certificate name is required")
    private String name;

    private String issuingOrganization;
    private LocalDate issueDate;
    private LocalDate expiryDate;
}