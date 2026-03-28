package org.abdev.rtfm.service.impl;

import org.abdev.rtfm.exception.BadInputException;
import org.abdev.rtfm.service.EmbeddingService;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class EmbeddingServiceImpl implements EmbeddingService {

    private final VectorStore vectorStore;

    public EmbeddingServiceImpl(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    /*
        ETL pipeline :
            Extract :
                - Read document
            Transform :
                - Split into chunks
                - Transform each chunk into Vectors (embedding)
            Load :
                - Store vectors in Database that support vectors
     */
    @Override
    public void processEmbedding(MultipartFile document) throws IOException {
        if (document == null || document.isEmpty()) {
            throw new BadInputException("Document must not be NULL");
        }

        List<Document> documents = this.readDocument(document);

        documents.forEach(doc -> {
            List<Document> chunks = splitDocumentIntoChunks(doc);
            vectorStore.add(chunks);
        });
    }

    @Override
    public List<Document> readDocument(MultipartFile document) {
        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(document.getResource());
        return tikaDocumentReader.read();
    }

    @Override
    public List<Document> splitDocumentIntoChunks(Document document) {
        TokenTextSplitter tokenTextSplitter = TokenTextSplitter
                .builder()
                .withChunkSize(500)
                .build();

        return tokenTextSplitter.split(document);
    }

    @Override
    public List<Document> splitDocumentsIntoChunks(List<Document> documents) {
        TokenTextSplitter tokenTextSplitter = TokenTextSplitter
                .builder()
                .withChunkSize(500)
                .build();

        return tokenTextSplitter.split(documents);
    }
}
