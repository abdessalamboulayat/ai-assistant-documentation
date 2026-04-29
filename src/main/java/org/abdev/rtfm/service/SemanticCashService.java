package org.abdev.rtfm.service;

import org.springframework.ai.document.Document;

import java.util.List;

public interface SemanticCashService {

    void storeQuestionAndAnswerInCash(String question, String answer);
    String getAnswerFromCash(String question);
    boolean answerAlreadyStored(String question);
    List<Document> searchAnswerInCash(String question);
}
