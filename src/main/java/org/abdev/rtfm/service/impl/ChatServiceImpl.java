package org.abdev.rtfm.service.impl;

import jakarta.servlet.http.HttpSession;
import org.abdev.rtfm.advisors.LongTermMemoryAdvisor;
import org.abdev.rtfm.dto.*;
import org.abdev.rtfm.service.ChatService;
import org.abdev.rtfm.util.Prompts;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ChatServiceImpl implements ChatService {

    private static final String USER_ID = "abdevtech";
    private final VectorStore vectorStore;
    @Qualifier("googleGenAiChatModel")
    private final ChatClient chatClient;
    private final LongTermMemoryService longTermMemoryService;
    private final double similarityTreshold;

    public ChatServiceImpl(VectorStore vectorStore,
                           ChatClient chatClient,
                           LongTermMemoryService longTermMemoryService,
                           @Value("${rag.similarity.treshold}") double similarityTreshold) {
        this.vectorStore = vectorStore;
        this.chatClient = chatClient;
        this.longTermMemoryService = longTermMemoryService;
        this.similarityTreshold = similarityTreshold;
    }

    @Override
    public List<Document> getContext(String query) {
        return vectorStore.similaritySearch(SearchRequest.builder()
                .query(query)
                .similarityThreshold(this.similarityTreshold)
                .topK(5)
                .build());
    }

    public ChatResponseDto sendMessage(HttpSession session, String message) {
        Prompt prompt = Prompts.promptTemplate().create(Map.of("message", message));

        LLMResponse llmResponse = this.chatClient.prompt(prompt)
                .advisors(advisorSpec -> {
                    advisorSpec.param(ChatMemory.CONVERSATION_ID, session.getId());
                    advisorSpec.param(LongTermMemoryAdvisor.USER_ID, USER_ID);
                })
                .call()
                .entity(LLMResponse.class);

        if (llmResponse == null) return null;

        // Extract memories elements and save for long term
        List<ExtractMemory> extractMemories = llmResponse.extractMemories() != null ? llmResponse.extractMemories() : List.of();
        for (ExtractMemory mem : extractMemories) {
            this.longTermMemoryService.save(USER_ID, mem);
        }

        // Handle user preferences
        String followUpQuestion = null;
        PreferenceSignal signal = llmResponse.preferenceSignal();
        if (signal != null && signal.followUpQuestion() != null) {
            // Store pending confirmation in session
            session.setAttribute("pending_preference_question", signal.followUpQuestion());
            session.setAttribute("pending_preference_topic", signal.topic());
            followUpQuestion = signal.followUpQuestion();
        }

        return new ChatResponseDto(llmResponse.answer(), llmResponse.extractMemories(), followUpQuestion);
    }
}
