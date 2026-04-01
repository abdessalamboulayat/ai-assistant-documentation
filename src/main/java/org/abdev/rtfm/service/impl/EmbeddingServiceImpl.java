package org.abdev.rtfm.service.impl;

import org.abdev.rtfm.exception.BadInputException;
import org.abdev.rtfm.service.EmbeddingService;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class EmbeddingServiceImpl implements EmbeddingService {

    private final VectorStore vectorStore;
    private final TextSplitter tokenTextSplitter;

    public EmbeddingServiceImpl(VectorStore vectorStore, TextSplitter tokenTextSplitter) {
        this.vectorStore = vectorStore;
        this.tokenTextSplitter = tokenTextSplitter;
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
        List<Document> chunks = this.tokenTextSplitter.split(documents);
        vectorStore.add(chunks);
    }

    @Override
    public List<Document> readDocument(MultipartFile document) {
        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(document.getResource());
        return tikaDocumentReader.read();
    }

}
