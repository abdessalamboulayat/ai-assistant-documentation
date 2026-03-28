package org.abdev.rtfm.service;

import org.springframework.ai.chat.model.ChatResponse;

public interface SemanticCashingService {

    void storeQuestionAndAnswerInCash(String question, String answer);
    String getAnswerFromCash(String question);
}
