package com.recruitment.platform.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Abstracts file storage operations.
 * Dev profile: LocalStorageService  — Docker volume
 * Prod profile: S3StorageService    — AWS S3
 */
public interface StorageService {

    /**
     * Stores the uploaded file and returns the storage path.
     * Dev  → absolute local path  e.g. "uploads/cvs/&lt;applicantId&gt;_filename.pdf"
     * Prod → S3 object key        e.g. "cvs/&lt;applicantId&gt;/filename.pdf"
     */
    String store(MultipartFile file, UUID applicantId);

    /**
     * Loads a file by its storage path as a Spring Resource.
     * Used by the cv.uploaded consumer to fetch file bytes for AI extraction.
     */
    Resource load(String filePath);

    /**
     * Deletes a file by its storage path.
     * Called as a compensating action when the DB transaction fails
     * after the file has already been stored (Phase 1 rollback).
     */
    void delete(String filePath);
}