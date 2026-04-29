package org.abdev.rtfm.advisors;

import jakarta.servlet.http.HttpSession;
import org.abdev.rtfm.dto.ExtractMemory;
import org.abdev.rtfm.dto.InformationType;
import org.abdev.rtfm.service.impl.LongTermMemoryService;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;

import java.util.Map;

public class UserPreferenceAdvisor implements CallAdvisor {

    private static final String ADVISOR_NAME = "user-preference-advisor";
    private static final int ORDER = -300;
    private static final String USER_ID = "userId";
    private static final String PENDING_PREFERENCE_QUESTON = "pending_preference_question";
    private static final String PENDING_PREFERENCE_TOPIC = "pending_preference_topic";
    private final String defaultUserId = "public:user";

    private final LongTermMemoryService longTermMemoryService;
    private final HttpSession httpSession;

    public UserPreferenceAdvisor(LongTermMemoryService longTermMemoryService, HttpSession httpSession) {
        this.longTermMemoryService = longTermMemoryService;
        this.httpSession = httpSession;
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        String userMessage = chatClientRequest.prompt().getUserMessage().getText();

        // Check if user is answering a pending preference question
        String pendingQuestion = (String) httpSession.getAttribute(PENDING_PREFERENCE_QUESTON);
        if (pendingQuestion != null && isConfirmation(userMessage)) {
            String topic = (String) httpSession.getAttribute(PENDING_PREFERENCE_TOPIC);
            longTermMemoryService.save(getUserId(chatClientRequest.context()),
                    new ExtractMemory(InformationType.SEMANTIC_MEMORY, topic));
            httpSession.removeAttribute(PENDING_PREFERENCE_QUESTON);
            httpSession.removeAttribute(PENDING_PREFERENCE_TOPIC);
        } else if (pendingQuestion != null) {
            // User ignored or corrected — clear the pending state
            httpSession.removeAttribute(PENDING_PREFERENCE_QUESTON);
            httpSession.removeAttribute(PENDING_PREFERENCE_TOPIC);
        }

        return callAdvisorChain.nextCall(chatClientRequest);
    }

    private boolean isConfirmation(String userMessage) {
        String messageLowercase = userMessage.toLowerCase();

        return messageLowercase.contains("yes") || messageLowercase.contains("sure")
                || messageLowercase.contains("ok") || messageLowercase.contains("please")
                || messageLowercase.contains("always");
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
