package com.recruitment.platform.service.ai;

import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("ollama")
public class OllamaChatOptionsStrategy implements AiChatOptionsStrategy {

    @Value("${spring.ai.ollama.chat.model}")
    private String modelName;

    @Override
    public ChatOptions buildOptions(BeanOutputConverter<?> converter) {
        return OllamaChatOptions.builder()
                .model(modelName)
                .outputSchema(converter.getJsonSchema())
                .build();
    }
}