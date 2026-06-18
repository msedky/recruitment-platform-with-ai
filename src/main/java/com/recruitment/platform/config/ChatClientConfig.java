package com.recruitment.platform.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class ChatClientConfig {

    /**
     * Dev profile — uses Ollama (local).
     * Explicitly qualifies "ollamaChatModel" so Spring doesn't get confused
     * when both Ollama and Bedrock starters are on the classpath.
     */
    @Bean
    @Profile("dev")
    public ChatClient devChatClient(@Qualifier("ollamaChatModel") ChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }

    /**
     * Prod profile — uses AWS Bedrock.
     * Explicitly qualifies "bedrockProxyChatModel".
     */
    @Bean
    @Profile("prod")
    public ChatClient prodChatClient(@Qualifier("bedrockProxyChatModel") ChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }
}