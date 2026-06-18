package com.recruitment.platform.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

/**
 * Published to cv.uploaded queue by the OutboxScheduler.
 * The consumer uses filePath to fetch the file from storage — raw file
 * content is never placed on the queue.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CvUploadedEvent implements Serializable {

    private UUID applicantId;         // links back to ApplicantEntity
    private String filePath;          // storage path — Volume (dev) or S3 object key (prod)
    private String originalFileName;  // e.g. "Mohammad_Sedky_CV.pdf"
    private String fileType;          // e.g. "application/pdf" or "application/vnd.openxmlformats..."
    private Long fileSize;            // bytes
}