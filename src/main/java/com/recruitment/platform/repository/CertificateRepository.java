package com.recruitment.platform.repository;

import com.recruitment.platform.model.entity.CertificateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface CertificateRepository extends JpaRepository<CertificateEntity, UUID> {

    Set<CertificateEntity> findByApplicantId(UUID applicantId);
}