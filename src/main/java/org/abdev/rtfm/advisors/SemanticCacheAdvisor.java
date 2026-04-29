package org.abdev.rtfm.advisors;

import org.abdev.rtfm.service.SemanticCashService;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;

import java.util.Comparator;
import java.util.List;

public class SemanticCacheAdvisor implements CallAdvisor {

    private static final String ADVISOR_NAME = "semantic-cash-advisor";
    private static final int ORDER = -200;

    private final SemanticCashService semanticCashService;

    public SemanticCacheAdvisor(SemanticCashService semanticCashService) {
        this.semanticCashService = semanticCashService;
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {

        // Search for answer if question already answered.
        List<Document> documents = this.semanticCashService.searchAnswerInCash(
                chatClientRequest.prompt().getUserMessage().getText());

        if (!documents.isEmpty()) {
            Document bestMatch = documents.stream().max(Comparator.comparing(Document::getScore)).orElse(null);
            if (bestMatch != null) {
                String answer = (String) bestMatch.getMetadata().get("answer");
                return ChatClientResponse.builder()
                        .chatResponse(ChatResponse.builder()
                                .generations(
                                        List.of(new Generation(new AssistantMessage(answer)))).build())
                        .build();
            }
        }

        return callAdvisorChain.nextCall(chatClientRequest);
    }

    @Override
    public String getName() {
        return ADVISOR_NAME;
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
