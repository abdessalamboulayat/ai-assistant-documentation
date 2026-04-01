package org.abdev.rtfm.config;

import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TextSplitterConfig {

    @Bean
    public TextSplitter tokenTextSplitterConfig() {
        return TokenTextSplitter.builder()
                .withChunkSize(500)
                .build();
    }
}
