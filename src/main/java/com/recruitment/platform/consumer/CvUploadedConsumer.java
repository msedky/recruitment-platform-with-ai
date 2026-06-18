package com.recruitment.platform.consumer;

import com.recruitment.platform.model.dto.ApplicantDTO;
import com.recruitment.platform.model.enums.ApplicantStatus;
import com.recruitment.platform.model.event.CvExtractedEvent;
import com.recruitment.platform.model.event.CvUploadedEvent;
import com.recruitment.platform.service.AiExtractionService;
import com.recruitment.platform.service.CvParserService;
import com.recruitment.platform.service.CvPersistenceService;
import com.recruitment.platform.service.CvStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CvUploadedConsumer {

    private final CvStorageService cvStorageService;
    private final CvParserService cvParserService;
    private final AiExtractionService aiExtractionService;
    private final CvPersistenceService cvPersistenceService;
    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.queues.cv-extracted.routing-key}")
    private String cvExtractedRoutingKey;

    /**
     * Phase 3 — cv.uploaded consumer (AI extraction + DB persistence).
     *
     * NO @Transactional on this method — intentional.
     * The AI call can take 30-120 seconds. Holding a DB transaction open
     * during that time causes idle-in-transaction timeouts on PostgreSQL.
     * Each DB operation uses its own short-lived transaction via CvPersistenceService.
     *
     * Happy path:
     * 1. Short TX: set status = PROCESSING
     * 2. Load file from storage (no DB)
     * 3. Extract plain text via CvParserService (no DB)
     * 4. Call AI — can take minutes (no DB connection held)
     * 5. Short TX: persist extracted data + all child entities + status = EXTRACTION_DONE
     * 6. Publish lightweight CvExtractedEvent (applicantId only)
     * 7. Ack message
     *
     * Failure paths:
     * - AI unavailable   → AiServiceUnavailableException → nack → retry → DLQ
     * - AI parse failure → AiResponseParseException      → nack → DLQ
     * - DB down (step 5) → exception → nack → retry (AI called again — unavoidable)
     */
    @RabbitListener(queues = "${app.rabbitmq.queues.cv-uploaded.name}")
    public void consume(@Payload CvUploadedEvent event) {
        log.info("cv.uploaded consumer received event — applicantId: {}", event.getApplicantId());

        // Step 1 — short TX: mark as PROCESSING (connection released immediately after)
        cvPersistenceService.updateStatus(event.getApplicantId(), ApplicantStatus.PROCESSING);

        // Step 2 — load file (no DB)
        Resource fileResource = cvStorageService.load(event.getFilePath());

        // Step 3 — extract plain text (no DB)
        String cvText = cvParserService.extractText(fileResource, event.getFileType());
        log.info("CV text extracted — applicantId: {}", event.getApplicantId());

        // Step 4 — AI call (no DB connection held — safe for long-running inference)
        ApplicantDTO dto = aiExtractionService.extractApplicantData(cvText);
        log.info("AI extraction completed — applicantId: {}", event.getApplicantId());

        // Step 5 — short TX: persist extracted data to DB (connection opens and closes here)
        cvPersistenceService.persistExtractedData(event.getApplicantId(), dto);

        // Step 6 — publish lightweight event (DB already committed — data safe)
        CvExtractedEvent extractedEvent = CvExtractedEvent.builder()
                .applicantId(event.getApplicantId())
                .build();
        rabbitTemplate.convertAndSend(exchange, cvExtractedRoutingKey, extractedEvent);
        log.info("CvExtractedEvent published — applicantId: {}", event.getApplicantId());

        // Step 7 — message acked automatically on method return
    }
}