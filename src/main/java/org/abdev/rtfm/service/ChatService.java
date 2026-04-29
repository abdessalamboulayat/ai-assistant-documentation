package org.abdev.rtfm.service;

import jakarta.servlet.http.HttpSession;
import org.abdev.rtfm.dto.ChatResponseDto;
import org.springframework.ai.document.Document;

import java.util.List;

public interface ChatService {
    List<Document> getContext(String query);
    //String askQuestion(String question, HttpSession session) throws JsonProcessingException;
    ChatResponseDto sendMessage(HttpSession session, String message);
}
