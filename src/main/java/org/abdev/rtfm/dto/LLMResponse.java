package org.abdev.rtfm.dto;

import java.util.List;

public record LLMResponse (
        String answer,
        List<ExtractMemory> extractMemories,
        PreferenceSignal preferenceSignal
) {

}
