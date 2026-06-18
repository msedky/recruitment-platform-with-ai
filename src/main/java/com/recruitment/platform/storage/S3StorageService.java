package com.recruitment.platform.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@Profile("prod")
@RequiredArgsConstructor
public class S3StorageService implements StorageService {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Override
    public String store(MultipartFile file, UUID applicantId) {
        if (file.isEmpty()) {
            throw new StorageException("Cannot store empty file");
        }

        String originalFilename = sanitiseFilename(file.getOriginalFilename());
        String objectKey = "cvs/" + applicantId + "/" + originalFilename;

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(objectKey)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(request, RequestBody.fromInputStream(
                    file.getInputStream(), file.getSize()));

            log.debug("Stored file in S3: s3://{}/{}", bucket, objectKey);
            return objectKey;

        } catch (IOException | S3Exception e) {
            throw new StorageException("Failed to upload file to S3: " + originalFilename, e);
        }
    }

    @Override
    public Resource load(String filePath) {
        try {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(filePath)
                    .build();

            return new InputStreamResource(s3Client.getObject(request));

        } catch (S3Exception e) {
            throw new StorageException("Failed to load file from S3: " + filePath, e);
        }
    }

    @Override
    public void delete(String filePath) {
        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(filePath)
                    .build();

            s3Client.deleteObject(request);
            log.debug("Deleted S3 object: s3://{}/{}", bucket, filePath);

        } catch (S3Exception e) {
            log.error("Failed to delete S3 object: {}", filePath, e);
        }
    }

    private String sanitiseFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            return "unnamed";
        }
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}