package org.abdev.rtfm.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.ai.document.Document;

import java.util.List;

public interface ChatService {
    List<Document> getContext(String query);
    String askQuestion(String question) throws JsonProcessingException;
}
