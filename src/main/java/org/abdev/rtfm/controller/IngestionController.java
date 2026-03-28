package org.abdev.rtfm.controller;

import org.abdev.rtfm.service.EmbeddingService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/documents")
public class IngestionController {

    private final EmbeddingService embeddingService;

    public IngestionController(EmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }

    @PostMapping
    public void embedDocument(@RequestParam("document") MultipartFile document) throws IOException {
        this.embeddingService.processEmbedding(document);
    }
}
