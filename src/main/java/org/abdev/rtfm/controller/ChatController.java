package org.abdev.rtfm.controller;

import jakarta.servlet.http.HttpSession;
import org.abdev.rtfm.dto.ChatResponseDto;
import org.abdev.rtfm.service.ChatService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ChatResponseDto askQuestion(HttpSession session, @RequestParam("question") String question) {
        return this.chatService.sendMessage(session, question);
    }
}
