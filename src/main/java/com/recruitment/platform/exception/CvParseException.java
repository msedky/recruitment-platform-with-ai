package com.recruitment.platform.exception;

/**
 * Thrown when text extraction from a CV file fails.
 * The cv.uploaded consumer treats this as a non-retryable failure —
 * retrying the same file will produce the same result.
 */
public class CvParseException extends RuntimeException {

    public CvParseException(String message, Throwable cause) {
        super(message, cause);
    }
}