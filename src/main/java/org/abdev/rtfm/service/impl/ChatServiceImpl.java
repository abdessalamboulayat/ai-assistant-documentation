package org.abdev.rtfm.service.impl;

import jakarta.servlet.http.HttpSession;
import org.abdev.rtfm.dto.ChatMessage;
import org.abdev.rtfm.mapper.DocumentMapper;
import org.abdev.rtfm.service.ChatService;
import org.abdev.rtfm.service.SemanticCashingService;
import org.abdev.rtfm.util.Prompts;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    private final VectorStore vectorStore;
    private final ChatModel chatModel;
    private final SemanticCashingService semanticCashingService;
    private final SessionMemoryService sessionMemoryService;

    public ChatServiceImpl(VectorStore vectorStore,
                           ChatModel chatModel,
                           SemanticCashingService semanticCashingService,
                           SessionMemoryService sessionMemoryService) {
        this.vectorStore = vectorStore;
        this.chatModel = chatModel;
        this.semanticCashingService = semanticCashingService;
        this.sessionMemoryService = sessionMemoryService;
    }

    @Override
    public List<Document> getContext(String query) {
        return vectorStore.similaritySearch(SearchRequest.builder().query(query).build());
    }

    @Override
    public String askQuestion(String question, HttpSession session) {
        if (question != null && question.isEmpty()) return null;

        String answerFromCash = this.semanticCashingService.getAnswerFromCash(question);

        if (answerFromCash != null && !answerFromCash.isEmpty()) {
            return answerFromCash;
        }

        String sessionId = session.getId();
        String recentMessages = this.getRecentMessages(sessionId);

        List<Document> documents = this.getContext(question);
        String context = DocumentMapper.mapDocumentListToString(documents);

        String answer = chatModel.call(Prompts.getPrompt(question, context, recentMessages));

        this.sessionMemoryService.addMessage(sessionId, new ChatMessage(question, answer));
        this.semanticCashingService.storeQuestionAndAnswerInCash(question, answer);
        return answer;
    }

    public String getRecentMessages(String sessionId) {
        StringBuilder recentMessages = new StringBuilder();
        List<Object> chatMessages = this.sessionMemoryService.getMessages(sessionId);

        chatMessages.forEach(chatMessage -> {
            recentMessages.append(((ChatMessage) chatMessage).question());
            recentMessages.append(((ChatMessage) chatMessage).answer());
        });

        return recentMessages.toString();
    }
}
