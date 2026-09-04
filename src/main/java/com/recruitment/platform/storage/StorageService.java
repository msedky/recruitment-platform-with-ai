package com.recruitment.platform.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface StorageService {

    String store(MultipartFile file, UUID applicantId);

    Resource load(String filePath);

    void delete(String filePath);
}