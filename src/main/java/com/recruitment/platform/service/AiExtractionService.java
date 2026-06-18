package com.recruitment.platform.service;

import com.recruitment.platform.model.dto.ApplicantDTO;

public interface AiExtractionService {

    ApplicantDTO extractApplicantData(String cvText);
}