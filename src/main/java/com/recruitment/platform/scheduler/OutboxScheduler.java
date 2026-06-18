package com.recruitment.platform.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recruitment.platform.model.entity.OutboxEntity;
import com.recruitment.platform.model.enums.ApplicantStatus;
import com.recruitment.platform.model.enums.OutboxStatus;
import com.recruitment.platform.repository.ApplicantRepository;
import com.recruitment.platform.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxScheduler {

    private final OutboxRepository outboxRepository;
    private final ApplicantRepository applicantRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.rabbitmq.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.queues.cv-uploaded.routing-key}")
    private String cvUploadedRoutingKey;

    @Value("${app.outbox.scheduler.max-retries}")
    private int maxRetries;

    /**
     * Runs every N milliseconds (configured via app.outbox.scheduler.interval-ms).
     * Fetches all PENDING outbox records and attempts to publish each to the broker.
     *
     * On success  → OutboxEntity.status = PUBLISHED, ApplicantEntity.status = PUBLISHED
     * On failure  → increment retryCount, update lastAttemptAt, keep PENDING
     * On max retry exceeded → OutboxEntity.status = FAILED, ERROR log
     */
    @Scheduled(fixedDelayString = "${app.outbox.scheduler.interval-ms}")
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEntity> pendingRecords = outboxRepository.findByStatus(OutboxStatus.PENDING);

        if (pendingRecords.isEmpty()) {
            return;
        }

        log.debug("Outbox scheduler — found {} PENDING record(s)", pendingRecords.size());

        for (OutboxEntity outbox : pendingRecords) {
            processOutboxRecord(outbox);
        }
    }

    private void processOutboxRecord(OutboxEntity outbox) {
        if (outbox.getRetryCount() >= maxRetries) {
            log.error("Outbox record {} exceeded max retries ({}) — marking as FAILED. " +
                            "aggregateId: {}, eventType: {}",
                    outbox.getId(), maxRetries,
                    outbox.getAggregateId(), outbox.getEventType());

            outbox.setStatus(OutboxStatus.FAILED);
            outboxRepository.save(outbox);
            return;
        }

        try {
            Object payload = deserialisePayload(outbox);

            rabbitTemplate.convertAndSend(exchange, cvUploadedRoutingKey, payload);

            outbox.setStatus(OutboxStatus.PUBLISHED);
            outboxRepository.save(outbox);

            updateApplicantStatus(outbox, ApplicantStatus.PUBLISHED);

            log.info("Outbox record {} published successfully — applicantId: {}",
                    outbox.getId(), outbox.getAggregateId());

        } catch (Exception e) {
            outbox.setRetryCount(outbox.getRetryCount() + 1);
            outbox.setLastAttemptAt(Instant.now());
            outboxRepository.save(outbox);

            log.warn("Outbox record {} failed to publish (attempt {}/{}) — will retry. Error: {}",
                    outbox.getId(),
                    outbox.getRetryCount(),
                    maxRetries,
                    e.getMessage());
        }
    }

    private Object deserialisePayload(OutboxEntity outbox) {
        try {
            return objectMapper.readValue(outbox.getPayload(), Object.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Failed to deserialise outbox payload for record: " + outbox.getId(), e);
        }
    }

    private void updateApplicantStatus(OutboxEntity outbox, ApplicantStatus status) {
        applicantRepository.findById(outbox.getAggregateId()).ifPresentOrElse(
                applicant -> {
                    applicant.setStatus(status);
                    applicantRepository.save(applicant);
                },
                () -> log.warn("Applicant not found for outbox aggregateId: {}",
                        outbox.getAggregateId())
        );
    }
}