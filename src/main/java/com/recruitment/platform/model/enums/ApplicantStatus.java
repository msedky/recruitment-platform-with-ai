package com.recruitment.platform.model.enums;

public enum ApplicantStatus {

    // --- lifecycle states ---
    UPLOAD_RECEIVED,   // file saved to storage, outbox record created
    PUBLISHED,         // outbox scheduler published event to cv.uploaded queue
    PROCESSING,        // cv.uploaded consumer picked up the message, calling AI
    EXTRACTION_DONE,   // AI extraction succeeded, CvExtractedEvent published to cv.extracted queue
    INDEX_PENDING,     // PostgreSQL write succeeded, ES indexing not yet done
    PROCESSED,         // both PostgreSQL and ES completed successfully

    // --- failure states ---
    AI_FAILED,         // cv.uploaded DLQ exhausted — AI permanently unavailable
    DB_FAILED,         // cv.extracted DLQ exhausted — PostgreSQL permanently unavailable
    INDEX_FAILED       // ES retry scheduler exhausted max retries — ERROR log + CloudWatch alert
}