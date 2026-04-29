package org.abdev.rtfm.advisors;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Map;

public class LongTermMemoryAdvisor implements CallAdvisor {

    private static final String ADVISOR_NAME = "user-preference-advisor";
    private static final int ORDER = -100;
    public static final String USER_ID = "userId";
    private final String defaultUserId = "public:user";

    private final VectorStore longTermMemoryVectorStore;

    @Value("${rag.similarity.treshold}")
    private double similarityTreshold;

    public LongTermMemoryAdvisor(VectorStore longTermMemoryVectorStore) {
        this.longTermMemoryVectorStore = longTermMemoryVectorStore;
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {

        String userId = this.getUserId(chatClientRequest.context());

        List<Document> documents = this.longTermMemoryVectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(chatClientRequest.prompt().getUserMessage().getText())
                        .similarityThreshold(this.similarityTreshold)
                        .topK(5)
                        .filterExpression("userId == '" + userId + "'")
                        .build());

        Prompt prompt = this.injectLongTermMemoryInPrompt(chatClientRequest, documents);

        ChatClientRequest request = chatClientRequest.mutate().prompt(prompt).build();

        return callAdvisorChain.nextCall(request);
    }

    private Prompt injectLongTermMemoryInPrompt(ChatClientRequest chatClientRequest, List<Document> documents) {
        StringBuilder memoryBlockBuilder = new StringBuilder();

        memoryBlockBuilder.append("Use the MEMORY below if relevant. Keep answers factual and concise.\n");
        memoryBlockBuilder.append("----- MEMORY -----\n");

        for (int i = 0; i < documents.size(); i++) {
            var m = documents.get(i);
            memoryBlockBuilder.append(i + 1)
                    .append(". ")
                    .append(m.getFormattedContent())
                    .append("\n");
        }

        return chatClientRequest.prompt().augmentSystemMessage(sys -> {
            String existing = sys.getText();
            StringBuilder textContent = new StringBuilder();
            textContent.append(memoryBlockBuilder).append("\n");

            if (existing != null && !existing.isBlank()) {
                textContent.append(existing).append("\n");
            }

            return sys.mutate().text(textContent.toString()).build();
        });
    }

    public String getUserId(Map<String, Object> context) {
        return context.containsKey(USER_ID) ? context.get(USER_ID).toString()
                : defaultUserId;
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
