package com.recruitment.platform.consumer;

import com.recruitment.platform.model.enums.ApplicantStatus;
import com.recruitment.platform.model.event.CvExtractedEvent;
import com.recruitment.platform.repository.ApplicantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CvExtractedDlqListener {

    private final RabbitTemplate rabbitTemplate;
    private final ApplicantRepository applicantRepository;

    @Value("${app.rabbitmq.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.queues.cv-extracted.routing-key}")
    private String cvExtractedRoutingKey;

    @Value("${app.dlq.requeue-delay-ms}")
    private long requeueDelayMs;

    /**
     * Listens on cv.extracted.dlq.
     * Triggered when PostgreSQL is down and cv.extracted consumer
     * exhausts all retries.
     * Waits for the configured delay then re-publishes back to
     * cv.extracted queue — AI is never re-called since the full
     * ApplicantDTO already travels in the event payload.
     */
    @RabbitListener(queues = "${app.rabbitmq.queues.cv-extracted.dlq}")
    public void handleDlq(@Payload CvExtractedEvent event) {
        log.warn("cv.extracted.dlq received event — applicantId: {}. " +
                "Will re-queue after {}ms delay.", event.getApplicantId(), requeueDelayMs);

        updateApplicantStatus(event);

        try {
            Thread.sleep(requeueDelayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("DLQ listener interrupted during delay — re-queuing immediately");
        }

        rabbitTemplate.convertAndSend(exchange, cvExtractedRoutingKey, event);
        log.info("cv.extracted.dlq re-queued event — applicantId: {}", event.getApplicantId());
    }

    private void updateApplicantStatus(CvExtractedEvent event) {
        applicantRepository.findById(event.getApplicantId()).ifPresentOrElse(
                applicant -> {
                    applicant.setStatus(ApplicantStatus.DB_FAILED);
                    applicantRepository.save(applicant);
                },
                () -> log.warn("Applicant not found during DLQ handling — id: {}",
                        event.getApplicantId())
        );
    }
}