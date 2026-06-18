package com.recruitment.platform.repository;

import com.recruitment.platform.model.entity.WorkExperienceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface WorkExperienceRepository extends JpaRepository<WorkExperienceEntity, UUID> {

    Set<WorkExperienceEntity> findByApplicantId(UUID applicantId);
}