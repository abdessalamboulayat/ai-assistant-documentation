package org.abdev.rtfm.mapper;

import org.abdev.rtfm.dto.ChatResponseDto;
import org.springframework.ai.chat.model.ChatResponse;

public class ChatResponseMapper {

    public static ChatResponseDto mapChatResponseToDto(ChatResponse chatResponse) {
        ChatResponseDto chatResponseDto = new ChatResponseDto();
        chatResponseDto.setChatResponseMetadata(chatResponse.getMetadata());
        chatResponseDto.setResults(chatResponse.getResults());
        return chatResponseDto;
    }

    public static ChatResponse mapChatResponseDto(ChatResponseDto chatResponseDto) {
        return ChatResponse.builder()
                .generations(chatResponseDto.getResults())
                .metadata(chatResponseDto.getChatResponseMetadata())
                .build();
    }
}
