package com.recruitment.platform.exception;

/**
 * Thrown when Elasticsearch is unavailable during a search request.
 * Mapped to 503 Service Unavailable by GlobalExceptionHandler.
 * Unlike findById, search has no PostgreSQL fallback.
 */
public class SearchUnavailableException extends RuntimeException {

    public SearchUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}