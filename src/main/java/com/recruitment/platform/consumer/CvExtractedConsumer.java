package com.recruitment.platform.consumer;

import com.recruitment.platform.mapper.ApplicantMapper;
import com.recruitment.platform.model.document.ApplicantDocument;
import com.recruitment.platform.model.entity.ApplicantEntity;
import com.recruitment.platform.model.enums.ApplicantStatus;
import com.recruitment.platform.model.event.CvExtractedEvent;
import com.recruitment.platform.repository.ApplicantRepository;
import com.recruitment.platform.repository.ApplicantSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CvExtractedConsumer {

    private final ApplicantRepository applicantRepository;
    private final ApplicantSearchRepository applicantSearchRepository;
    private final ApplicantMapper applicantMapper;

    /**
     * Phase 4 — cv.extracted consumer (ES indexing only).
     *
     * Receives a lightweight CvExtractedEvent carrying only applicantId.
     * All extracted data is already in PostgreSQL (written by CvUploadedConsumer).
     * This consumer's sole responsibility is ES indexing.
     *
     * Happy path:
     * 1. Load ApplicantEntity from PostgreSQL by applicantId (includes all child entities)
     * 2. Set status = INDEX_PENDING
     * 3. Index ApplicantDocument to Elasticsearch
     * 4. Set status = PROCESSED
     * 5. Ack message
     *
     * Failure paths:
     * - DB down    → nack → retry → DLQ → re-queue after delay
     * - ES down    → DB write kept, status = INDEX_PENDING
     *                EsRetryScheduler retries indexing separately
     */
    @RabbitListener(queues = "${app.rabbitmq.queues.cv-extracted.name}")
    @Transactional
    public void consume(@Payload CvExtractedEvent event) {
        log.info("cv.extracted consumer received event — applicantId: {}", event.getApplicantId());

        // Step 1 — load fully populated ApplicantEntity from DB
        ApplicantEntity applicant = applicantRepository.findByIdWithChildren(event.getApplicantId())
                .orElseThrow(() -> new RuntimeException(
                        "ApplicantEntity not found for id: " + event.getApplicantId()));

        // Step 2 — mark INDEX_PENDING before attempting ES
        applicant.setStatus(ApplicantStatus.INDEX_PENDING);
        applicantRepository.save(applicant);

        // Step 3 — index to Elasticsearch
        // If ES is down this throws — @Transactional does NOT roll back DB write
        // (ES is not a JPA-managed resource) — status stays INDEX_PENDING
        // EsRetryScheduler picks it up and retries indexing later
        try {
            ApplicantDocument document = applicantMapper.toDocument(applicant);
            applicantSearchRepository.save(document);

            // Step 4 — both DB and ES succeeded
            applicant.setStatus(ApplicantStatus.PROCESSED);
            applicantRepository.save(applicant);
            log.info("Applicant indexed to ES — applicantId: {}", event.getApplicantId());

        } catch (Exception esException) {
            log.warn("ES indexing failed for applicantId: {} — status remains INDEX_PENDING. " +
                            "EsRetryScheduler will retry. Error: {}",
                    event.getApplicantId(), esException.getMessage());
            // Do NOT rethrow — DB write is safe, ES retried by scheduler
            // Message is acked normally
        }
    }
}