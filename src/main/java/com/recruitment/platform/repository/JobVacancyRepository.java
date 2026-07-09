package com.recruitment.platform.repository;

import com.recruitment.platform.model.entity.JobVacancyEntity;
import com.recruitment.platform.model.enums.VacancyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JobVacancyRepository extends JpaRepository<JobVacancyEntity, UUID> {
    Page<JobVacancyEntity> findByStatus(VacancyStatus status, Pageable pageable);
}