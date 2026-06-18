package com.recruitment.platform.scheduler;

import com.recruitment.platform.mapper.ApplicantMapper;
import com.recruitment.platform.model.document.ApplicantDocument;
import com.recruitment.platform.model.entity.ApplicantEntity;
import com.recruitment.platform.model.enums.ApplicantStatus;
import com.recruitment.platform.repository.ApplicantRepository;
import com.recruitment.platform.repository.ApplicantSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EsRetryScheduler {

    private final ApplicantRepository applicantRepository;
    private final ApplicantSearchRepository applicantSearchRepository;
    private final ApplicantMapper applicantMapper;

    /**
     * Runs every N milliseconds (configured via app.es.retry.scheduler.interval-ms).
     * Fetches all applicants with status = INDEX_PENDING and retries ES indexing.
     *
     * On success → status = PROCESSED
     * On failure → keep INDEX_PENDING, retry next cycle, ERROR log for CloudWatch alerting
     */
    @Scheduled(fixedDelayString = "${app.es.retry.scheduler.interval-ms}")
    public void retryIndexPending() {
        List<ApplicantEntity> pending = applicantRepository
                .findByStatus(ApplicantStatus.INDEX_PENDING);

        if (pending.isEmpty()) {
            return;
        }

        log.info("ES retry scheduler — found {} INDEX_PENDING applicant(s)", pending.size());

        for (ApplicantEntity applicant : pending) {
            applicant = applicantRepository.findByIdWithChildren(applicant.getId()).get();
            retryIndex(applicant);
        }
    }

    private void retryIndex(ApplicantEntity applicant) {
        try {
            ApplicantDocument document = applicantMapper.toDocument(applicant);
            applicantSearchRepository.save(document);

            applicant.setStatus(ApplicantStatus.PROCESSED);
            applicantRepository.save(applicant);

            log.info("ES retry succeeded — applicantId: {}", applicant.getId());

        } catch (Exception e) {
            log.error("ES_INDEX_RETRY_FAILURE applicantId={} error={}",
                    applicant.getId(), e.getMessage());
        }
    }
}