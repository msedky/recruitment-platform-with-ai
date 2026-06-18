package com.recruitment.platform.model.entity;

import com.recruitment.platform.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "certificates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CertificateEntity extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String issuingOrganization;

    @Column(nullable = true)
    private LocalDate issueDate;

    @Column(nullable = true)
    private LocalDate expiryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    private ApplicantEntity applicant;
}