package com.recruitment.platform.controller;

import com.recruitment.platform.model.dto.ApplicantDTO;
import com.recruitment.platform.service.AiExtractionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/ai-extraction")
@RequiredArgsConstructor
public class AiExtractionController {
    private final AiExtractionService aiExtractionService;

    public ResponseEntity<ApplicantDTO> extractApplicantData(String cvText) {
        log.info("Received request to extract applicant data from CV text");
        return ResponseEntity.ok(aiExtractionService.extractApplicantData(cvText));
    }
}
