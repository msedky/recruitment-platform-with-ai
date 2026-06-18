package com.recruitment.platform.repository;

import com.recruitment.platform.model.entity.SkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface SkillRepository extends JpaRepository<SkillEntity, UUID> {

    Set<SkillEntity> findByApplicantId(UUID applicantId);
}