package com.recruitment.platform.consumer;

import com.recruitment.platform.model.enums.ApplicantStatus;
import com.recruitment.platform.model.event.CvUploadedEvent;
import com.recruitment.platform.repository.ApplicantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CvUploadedDlqListener {

    private final RabbitTemplate rabbitTemplate;
    private final ApplicantRepository applicantRepository;

    @Value("${app.rabbitmq.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.queues.cv-uploaded.routing-key}")
    private String cvUploadedRoutingKey;

    @Value("${app.dlq.requeue-delay-ms}")
    private long requeueDelayMs;

    /**
     * Listens on cv.uploaded.dlq.
     * Waits for the configured delay then re-publishes the event back
     * to cv.uploaded queue — allowing the system to retry once the
     * AI service has recovered.
     *
     * If the AI service is permanently unavailable, this listener
     * keeps re-queuing indefinitely. Add an alert/circuit-breaker
     * here for production hardening.
     */
    @RabbitListener(queues = "${app.rabbitmq.queues.cv-uploaded.dlq}")
    public void handleDlq(@Payload CvUploadedEvent event) {
        log.warn("cv.uploaded.dlq received event — applicantId: {}. " +
                "Will re-queue after {}ms delay.", event.getApplicantId(), requeueDelayMs);

        updateApplicantStatus(event.getApplicantId(), ApplicantStatus.AI_FAILED);

        try {
            Thread.sleep(requeueDelayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("DLQ listener interrupted during delay — re-queuing immediately");
        }

        rabbitTemplate.convertAndSend(exchange, cvUploadedRoutingKey, event);
        log.info("cv.uploaded.dlq re-queued event — applicantId: {}", event.getApplicantId());
    }

    private void updateApplicantStatus(UUID applicantId, ApplicantStatus status) {
        applicantRepository.findById(applicantId).ifPresentOrElse(
                applicant -> {
                    applicant.setStatus(status);
                    applicantRepository.save(applicant);
                },
                () -> log.warn("Applicant not found during DLQ handling — id: {}", applicantId)
        );
    }
}