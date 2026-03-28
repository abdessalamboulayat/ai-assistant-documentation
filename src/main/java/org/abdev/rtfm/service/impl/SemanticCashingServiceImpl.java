package org.abdev.rtfm.service.impl;

import org.abdev.rtfm.service.SemanticCashingService;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SemanticCashingServiceImpl implements SemanticCashingService {
    private final static double THRESHOLD_DEFAULT_VALUE = 0.90;

    private final VectorStore semanticCashingVectorSearch;
    private final double similarityTreshold = THRESHOLD_DEFAULT_VALUE;

    public SemanticCashingServiceImpl(VectorStore semanticCashingVectorSearch) {
        this.semanticCashingVectorSearch = semanticCashingVectorSearch;
    }

    @Override
    public void storeQuestionAndAnswerInCash(String question, String answer) {
        if (question == null || answer == null) return;
        Document document = new Document(question, Map.of("answer", answer));
        this.semanticCashingVectorSearch.add(List.of(document));
    }

    @Override
    public String getAnswerFromCash(String question) {
        if (question == null) return null;

        List<Document> documents = this.semanticCashingVectorSearch.similaritySearch(
                SearchRequest.builder()
                        .query(question)
                        .similarityThreshold(this.similarityTreshold)
                        .build()
        );

        Optional<Document> first = documents.stream()
                .filter(doc -> (doc != null) && (doc.getScore() > this.similarityTreshold))
                .findFirst();

        return first.map(document ->
                (String) document.getMetadata().get("answer")).orElse(null);

    }
}
