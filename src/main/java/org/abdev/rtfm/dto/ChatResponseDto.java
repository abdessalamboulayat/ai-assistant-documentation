package org.abdev.rtfm.dto;

import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.model.Generation;

import java.time.Instant;
import java.util.List;

public class ChatResponseDto {
    private List<Generation> results;
    private ChatResponseMetadata chatResponseMetadata;

    public List<Generation> getResults() {
        return results;
    }

    public void setResults(List<Generation> results) {
        this.results = results;
    }

    public ChatResponseMetadata getChatResponseMetadata() {
        return chatResponseMetadata;
    }

    public void setChatResponseMetadata(ChatResponseMetadata chatResponseMetadata) {
        this.chatResponseMetadata = chatResponseMetadata;
    }
}
