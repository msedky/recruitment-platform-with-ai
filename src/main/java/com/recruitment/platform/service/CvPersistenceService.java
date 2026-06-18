package com.recruitment.platform.service;

import com.recruitment.platform.model.dto.ApplicantDTO;
import com.recruitment.platform.model.entity.ApplicantEntity;

import java.util.UUID;

public interface CvPersistenceService {

    /**
     * Updates the ApplicantEntity status — used for lightweight status-only updates
     * that should not hold a connection during long-running operations.
     */
    void updateStatus(UUID applicantId, com.recruitment.platform.model.enums.ApplicantStatus status);

    /**
     * Persists the full extracted ApplicantDTO into PostgreSQL.
     * Runs in its own @Transactional — called only after AI extraction completes,
     * so no DB connection is held during the AI call.
     */
    ApplicantEntity persistExtractedData(UUID applicantId, ApplicantDTO dto);
}