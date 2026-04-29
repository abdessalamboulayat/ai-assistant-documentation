package org.abdev.rtfm.advisors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.abdev.rtfm.dto.LLMResponse;
import org.abdev.rtfm.service.SemanticCashService;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;

public class AnswerStorageAdvisor implements CallAdvisor {

    private static final String ADVISOR_NAME = "answer-storage";
    private static final int ORDER = 0;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SemanticCashService semanticCashService;

    public AnswerStorageAdvisor(SemanticCashService semanticCashService) {
        this.semanticCashService = semanticCashService;
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        // normal call
        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);

        // extract userMessage and assistantMessage
        String userMessage = chatClientRequest.prompt().getUserMessage().getText();
        LLMResponse assistantMessage = null;
        ChatResponse chatResponse = chatClientResponse.chatResponse();
        if (chatResponse != null) {
            Generation result = chatResponse.getResult();
            if (result != null) {
                try {
                    assistantMessage = objectMapper.readValue(result.getOutput().getText(), LLMResponse.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if (this.semanticCashService.answerAlreadyStored(userMessage)) {
            return chatClientResponse;
        }

        if (assistantMessage != null) {
            this.semanticCashService.storeQuestionAndAnswerInCash(userMessage, assistantMessage.answer());
        }
        return chatClientResponse;
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
