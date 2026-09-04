package com.recruitment.platform.service.ai;

import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("anthropic")
public class AnthropicChatOptionsStrategy implements AiChatOptionsStrategy {

    @Value("${spring.ai.anthropic.chat.options.model}")
    private String modelName;

    @Override
    public ChatOptions buildOptions(BeanOutputConverter<?> converter) {
        return AnthropicChatOptions.builder()
                .model(modelName)
                .maxTokens(4096)
                .build();
    }

    @Override
    public String formatInstructions(BeanOutputConverter<?> converter) {
        return "\n\n" + converter.getFormat();
    }
}