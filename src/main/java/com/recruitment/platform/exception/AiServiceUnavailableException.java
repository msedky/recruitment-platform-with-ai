package com.recruitment.platform.exception;

/**
 * Thrown when the AI model is unreachable or returns a connection error.
 * The cv.uploaded consumer treats this as a retryable failure — nack → retry → DLQ.
 */
public class AiServiceUnavailableException extends RuntimeException {

    public AiServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}