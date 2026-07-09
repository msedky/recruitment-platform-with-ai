package com.recruitment.platform.service.impl;

import com.recruitment.platform.exception.AiResponseParseException;
import com.recruitment.platform.exception.AiServiceUnavailableException;
import com.recruitment.platform.model.dto.ApplicantDTO;
import com.recruitment.platform.service.AiExtractionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiExtractionServiceImpl implements AiExtractionService {

    private final ChatModel chatModel;

    @Value("${spring.ai.ollama.chat.model}")
    private String modelName;

    private static final String EXTRACTION_PROMPT = """
            You are an expert CV parser. Extract all information from the following CV text
            into the required structure.
            
            CV Text:
            %s
            """;

    @Override
    public ApplicantDTO extractApplicantData(String cvText) {
        BeanOutputConverter<ApplicantDTO> converter = new BeanOutputConverter<>(ApplicantDTO.class);
        String rawResponse = callAiModel(cvText, converter);
        return parseResponse(rawResponse, converter);
    }

    private String callAiModel(String cvText, BeanOutputConverter<ApplicantDTO> converter) {
        try {
            String promptText = String.format(EXTRACTION_PROMPT, cvText);

            OllamaChatOptions options = OllamaChatOptions.builder()
                    .model(modelName)
                    .outputSchema(converter.getJsonSchema())
                    .build();

            Prompt prompt = new Prompt(promptText, options);
            ChatResponse chatResponse = chatModel.call(prompt);
            String response = chatResponse.getResult().getOutput().getText();

            log.info("AI model responded successfully");
            return response;
        } catch (Exception e) {
            log.error("AI model call failed: {}", e.getMessage());
            throw new AiServiceUnavailableException("AI model is unavailable", e);
        }
    }

    private ApplicantDTO parseResponse(String rawResponse, BeanOutputConverter<ApplicantDTO> converter) {
        log.info("rawResponse = {}", rawResponse);
        try {
            ApplicantDTO result = converter.convert(rawResponse);
            log.info("AI extraction parsed successfully");
            return result;
        } catch (Exception e) {
            log.error("Failed to parse AI response into ApplicantDTO: {}", e.getMessage());
            throw new AiResponseParseException("AI response could not be parsed", e);
        }
    }
}