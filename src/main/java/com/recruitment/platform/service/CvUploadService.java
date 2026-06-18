package com.recruitment.platform.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface CvUploadService {

    /**
     * Phase 1 — synchronous upload flow.
     * Stores the CV file, persists a skeleton ApplicantEntity,
     * persists CvFileEntity, and creates an OutboxEntity for async publishing.
     *
     * @return pre-generated applicantId — returned to caller as 202 Accepted body
     */
    UUID handleUpload(MultipartFile file);
}