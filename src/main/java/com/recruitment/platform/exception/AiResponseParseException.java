package com.recruitment.platform.exception;

/**
 * Thrown when the AI model responds but the output cannot be parsed into ApplicantDTO.
 * The cv.uploaded consumer treats this as a non-retryable failure — nack → DLQ directly.
 * Repeated retries with the same CV text will likely produce the same bad output.
 */
public class AiResponseParseException extends RuntimeException {

    public AiResponseParseException(String message, Throwable cause) {
        super(message, cause);
    }
}