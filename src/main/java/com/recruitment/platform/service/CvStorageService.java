package com.recruitment.platform.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface CvStorageService {

    /**
     * Stores the uploaded file and returns the storage path.
     * Dev  → local file path   e.g. "uploads/cvs/&lt;uuid&gt;_filename.pdf"
     * Prod → S3 object key     e.g. "cvs/&lt;uuid&gt;/filename.pdf"
     */
    String store(MultipartFile file, UUID applicantId);

    /**
     * Loads a file by its storage path as a Spring Resource.
     * Used by CvUploadedConsumer to fetch file bytes for text extraction.
     * Dev  → UrlResource from local path
     * Prod → InputStreamResource from S3
     */
    Resource load(String filePath);

    /**
     * Deletes a file by its storage path.
     * Called as a compensating action if the DB transaction fails
     * after the file has already been stored.
     */
    void delete(String filePath);
}