package com.recruitment.platform.model.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.recruitment.platform.common.dto.BaseDTO;
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
public class CertificateDTO extends BaseDTO {

    private String name;
    @JsonAlias({"issuer", "organization", "issuing_organization"})
    private String issuingOrganization;
    @JsonAlias({"issue_date"})
    private LocalDate issueDate;
    @JsonAlias({"expiry_date", "expiration_date"})
    private LocalDate expiryDate;
}