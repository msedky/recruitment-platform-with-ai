package com.recruitment.platform.repository;

import com.recruitment.platform.model.entity.EducationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface EducationRepository extends JpaRepository<EducationEntity, UUID> {

    Set<EducationEntity> findByApplicantId(UUID applicantId);
}