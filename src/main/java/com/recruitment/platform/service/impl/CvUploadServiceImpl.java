package com.recruitment.platform.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recruitment.platform.exception.InvalidCvFileException;
import com.recruitment.platform.model.entity.ApplicantEntity;
import com.recruitment.platform.model.entity.CvFileEntity;
import com.recruitment.platform.model.entity.OutboxEntity;
import com.recruitment.platform.model.enums.ApplicantStatus;
import com.recruitment.platform.model.enums.OutboxStatus;
import com.recruitment.platform.model.event.CvUploadedEvent;
import com.recruitment.platform.repository.ApplicantRepository;
import com.recruitment.platform.repository.CvFileRepository;
import com.recruitment.platform.repository.OutboxRepository;
import com.recruitment.platform.service.CvStorageService;
import com.recruitment.platform.service.CvUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CvUploadServiceImpl implements CvUploadService {

    private final CvStorageService cvStorageService;
    private final ApplicantRepository applicantRepository;
    private final CvFileRepository cvFileRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    /**
     * Phase 1 — synchronous upload flow.
     *
     * Steps:
     * 1. Store file → get filePath
     * 2. Persist ApplicantEntity (status = UPLOAD_RECEIVED)   ┐
     * 3. Persist CvFileEntity                                  ├─ @Transactional
     * 4. Persist OutboxEntity  (status = PENDING)              ┘
     *
     * If the DB transaction fails after the file is already stored,
     * a compensating delete is triggered on the storage layer.
     *
     * @return pre-generated applicantId — returned to caller as 202 Accepted body
     */
    @Override
    @Transactional
    public UUID handleUpload(MultipartFile file) {
        validateFileType(file);

        UUID applicantId = UUID.randomUUID();
        String filePath = null;

        try {
            // Step 1 — store file (outside transaction — storage is not transactional)
            filePath = cvStorageService.store(file, applicantId);
            log.info("CV file stored at: {} for applicantId: {}", filePath, applicantId);

            // Step 2 — persist skeleton ApplicantEntity
            // fullName and email are NOT NULL in the schema but unknown until AI extraction.
            // Temporary placeholders are overwritten by CvExtractedConsumer (Phase 4).
            ApplicantEntity applicant = ApplicantEntity.builder()
                    .id(applicantId)
                    .status(ApplicantStatus.UPLOAD_RECEIVED)
                    .fullName("PENDING_EXTRACTION")
                    .email("PENDING_" + applicantId + "@extraction.local")
                    .build();
            applicantRepository.save(applicant);

            // Step 3 — persist CvFileEntity
            CvFileEntity cvFile = CvFileEntity.builder()
                    .id(UUID.randomUUID())
                    .applicant(applicant)
                    .originalFileName(file.getOriginalFilename())
                    .storedFileName(applicantId + "_" + file.getOriginalFilename())
                    .filePath(filePath)
                    .fileSize(file.getSize())
                    .fileType(file.getContentType())
                    .build();
            cvFileRepository.save(cvFile);

            // Step 4 — persist OutboxEntity
            CvUploadedEvent event = CvUploadedEvent.builder()
                    .applicantId(applicantId)
                    .filePath(filePath)
                    .originalFileName(file.getOriginalFilename())
                    .fileType(file.getContentType())
                    .fileSize(file.getSize())
                    .build();

            OutboxEntity outbox = OutboxEntity.builder()
                    .aggregateId(applicantId)
                    .eventType("CV_UPLOADED")
                    .payload(serialise(event))
                    .status(OutboxStatus.PENDING)
                    .build();
            outboxRepository.save(outbox);

            log.info("Upload phase complete — applicantId: {}, outbox record created", applicantId);
            return applicantId;

        } catch (Exception e) {
            // Compensating action — delete the file if DB commit fails
            if (filePath != null) {
                log.warn("DB transaction failed — deleting stored file: {}", filePath);
                tryDeleteFile(filePath);
            }
            throw e;
        }
    }

    private void validateFileType(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidCvFileException("Uploaded file is empty");
        }
        String contentType = file.getContentType();
        if (contentType == null ||
                (!contentType.equals("application/pdf") &&
                        !contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))) {
            throw new InvalidCvFileException(
                    "Unsupported file type: " + contentType + ". Only PDF and DOCX are accepted.");
        }
    }

    private void tryDeleteFile(String filePath) {
        try {
            cvStorageService.delete(filePath);
        } catch (Exception ex) {
            log.error("Compensating delete failed for path: {} — manual cleanup required",
                    filePath, ex);
        }
    }

    private String serialise(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialise outbox payload", e);
        }
    }
}