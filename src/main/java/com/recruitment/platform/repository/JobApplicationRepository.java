package com.recruitment.platform.repository;

import com.recruitment.platform.model.entity.JobApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplicationEntity, UUID> {
}
