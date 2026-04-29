package org.abdev.rtfm.config;

import jakarta.servlet.http.HttpSession;
import org.abdev.rtfm.advisors.AnswerStorageAdvisor;
import org.abdev.rtfm.advisors.LongTermMemoryAdvisor;
import org.abdev.rtfm.advisors.SemanticCacheAdvisor;
import org.abdev.rtfm.advisors.UserPreferenceAdvisor;
import org.abdev.rtfm.service.SemanticCashService;
import org.abdev.rtfm.service.impl.LongTermMemoryService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatConfig {

    @Bean
    public ChatClient chatClient(@Qualifier("googleGenAiChatModel") ChatModel chatModel,
                                 ChatMemory chatMemory,
                                 VectorStore vectorStore,
                                 SemanticCashService semanticCashService,
                                 LongTermMemoryService longTermMemoryService,
                                 VectorStore longTermMemoryVectorStore,
                                 HttpSession session) {
        return ChatClient.builder(chatModel)
                .defaultAdvisors(advisorSpec -> {
                    advisorSpec.advisors(
                            new UserPreferenceAdvisor(longTermMemoryService, session),
                            new SemanticCacheAdvisor(semanticCashService),
                            new LongTermMemoryAdvisor(longTermMemoryVectorStore),
                            MessageChatMemoryAdvisor.builder(chatMemory).build(),
                            QuestionAnswerAdvisor.builder(vectorStore).searchRequest(SearchRequest.builder().build()).build(),
                            new AnswerStorageAdvisor(semanticCashService));
                }).build();
    }
}
