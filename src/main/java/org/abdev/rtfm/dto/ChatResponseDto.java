package org.abdev.rtfm.dto;

import java.util.List;

public record ChatResponseDto(
        String answer,
        List<ExtractMemory> extractMemories,
        String followUpQuestion
) {
}
