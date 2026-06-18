package com.recruitment.platform.repository;

import com.recruitment.platform.model.entity.CvFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CvFileRepository extends JpaRepository<CvFileEntity, UUID> {

    Optional<CvFileEntity> findByApplicantId(UUID applicantId);
}