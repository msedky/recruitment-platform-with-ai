package com.recruitment.platform.service.ai;

import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("openai")
public class OpenAiChatOptionsStrategy implements AiChatOptionsStrategy {

    @Value("${spring.ai.openai.chat.model}")
    private String modelName;

    @Override
    public ChatOptions buildOptions(BeanOutputConverter<?> converter) {
        return OpenAiChatOptions.builder()
                .model(modelName)
                .build();
    }

    @Override
    public String formatInstructions(BeanOutputConverter<?> converter) {
        return "\n\n" + converter.getFormat();
    }
}