package org.abdev.rtfm.service;

import org.springframework.ai.document.Document;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface EmbeddingService {
    void processEmbedding(MultipartFile document) throws IOException;
    List<Document> readDocument(MultipartFile document);
    List<Document> splitDocumentIntoChunks(Document document);
    List<Document> splitDocumentsIntoChunks(List<Document> documents);
}
