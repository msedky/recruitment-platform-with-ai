package com.recruitment.platform.service;

import org.springframework.core.io.Resource;

public interface CvParserService {

    /**
     * Extracts plain text from a CV file.
     *
     * @param resource    Spring Resource — works for both local file (dev) and S3 stream (prod)
     * @param contentType MIME type — used to determine parser (PDF vs DOCX)
     *                    e.g. "application/pdf" or
     *                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
     * @return plain text extracted from the file
     * @throws CvParseException if extraction fails — non-retryable
     */
    String extractText(Resource resource, String contentType);
}