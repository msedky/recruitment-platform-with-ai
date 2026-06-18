package com.recruitment.platform.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recruitment.platform.exception.AiResponseParseException;
import com.recruitment.platform.exception.AiServiceUnavailableException;
import com.recruitment.platform.model.dto.ApplicantDTO;
import com.recruitment.platform.service.AiExtractionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiExtractionServiceImpl implements AiExtractionService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    private static final String EXTRACTION_PROMPT = """
            You are an expert CV parser. Extract all information from the following CV text
            and return ONLY a valid JSON object with NO extra text, NO markdown, NO backticks.
            
            The JSON must follow this exact structure:
            {
                "fullName": "string",
                "email": "string",
                "phone": "string or null",
                "nationality": "string or null",
                "address": "string or null",
                "linkedInUrl": "string or null",
                "portfolioUrl": "string or null",
                "summary": "string or null",
                "workExperiences": [
                    {
                        "companyName": "string",
                        "jobTitle": "string",
                        "roleType": "INDIVIDUAL_CONTRIBUTOR | TEAM_LEAD | MANAGER | SENIOR_MANAGER | DIRECTOR | EXECUTIVE",
                        "startDate": "YYYY-MM-DD",
                        "endDate": "YYYY-MM-DD or null",
                        "isCurrent": true or false,
                        "description": "string or null"
                    }
                ],
                "educations": [
                    {
                        "institution": "string",
                        "degreeType": "HIGH_SCHOOL | DIPLOMA | BACHELOR | MASTER | PHD | CERTIFICATION",
                        "fieldOfStudy": "string",
                        "startDate": "YYYY-MM-DD",
                        "endDate": "YYYY-MM-DD or null",
                        "isCurrent": true or false,
                        "grade": "string or null"
                    }
                ],
                "skills": [
                    {
                        "name": "string",
                        "level": "BEGINNER | INTERMEDIATE | EXPERT"
                    }
                ],
                "certificates": [
                    {
                        "name": "string",
                        "issuingOrganization": "string or null",
                        "issueDate": "YYYY-MM-DD or null",
                        "expiryDate": "YYYY-MM-DD or null"
                    }
                ],
                "languages": [
                    {
                        "name": "string",
                        "proficiency": "BEGINNER | INTERMEDIATE | ADVANCED | NATIVE"
                    }
                ]
            }
            
            CV Text:
            %s
            """;

    @Override
    public ApplicantDTO extractApplicantData(String cvText) {
        String rawResponse = callAiModel(cvText);
        return parseResponse(rawResponse);
    }

    private String callAiModel(String cvText) {
        try {
            String prompt = String.format(EXTRACTION_PROMPT, cvText);
            String response = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();
            log.info("AI model responded successfully");
            return response;
        } catch (Exception e) {
            // Any connection/timeout/model error is treated as service unavailable
            log.error("AI model call failed: {}", e.getMessage());
            throw new AiServiceUnavailableException("AI model is unavailable", e);
        }
    }

    private ApplicantDTO parseResponse(String rawResponse) {
        try {
            // Extract the JSON object between the first '{' and last '}'
            // This handles models that add preamble/postamble text despite instructions,
            // as well as markdown code fences — all in one step.
            int start = rawResponse.indexOf('{');
            int end = rawResponse.lastIndexOf('}');
            if (start == -1 || end == -1 || end < start) {
                log.error("No JSON object found in AI response: {}", rawResponse);
                throw new AiResponseParseException("AI response could not be parsed", null);
            }
            String cleaned = rawResponse.substring(start, end + 1).trim();
            log.info("cleaned = {}", cleaned);
            ApplicantDTO result = objectMapper.readValue(cleaned, ApplicantDTO.class);
            log.info("AI extraction parsed successfully");
            return result;

        } catch (JsonProcessingException e) {
            // Model responded but output is not valid JSON or does not match the DTO structure
            // Retrying the same CV text will likely produce the same bad output
            log.error("Failed to parse AI response into ApplicantDTO: {}", e.getMessage());
            throw new AiResponseParseException("AI response could not be parsed", e);
        }
    }
}