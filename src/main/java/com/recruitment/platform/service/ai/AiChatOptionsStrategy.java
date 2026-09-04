package com.recruitment.platform.service.ai;

import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.converter.BeanOutputConverter;

public interface AiChatOptionsStrategy {
    ChatOptions buildOptions(BeanOutputConverter<?> converter);

    default String formatInstructions(BeanOutputConverter<?> converter) {
        return "";
    }
}