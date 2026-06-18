package com.recruitment.platform.repository;

import com.recruitment.platform.model.entity.LanguageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface LanguageRepository extends JpaRepository<LanguageEntity, UUID> {

    Set<LanguageEntity> findByApplicantId(UUID applicantId);
}