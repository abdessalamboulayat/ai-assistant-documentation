package org.abdev.rtfm.service.impl;

import org.abdev.rtfm.dto.ExtractMemory;
import org.abdev.rtfm.service.RagMemoryService;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LongTermMemoryService implements RagMemoryService {

    private final static String PREFIX_KEY="long-term-memory:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final VectorStore longTermMemoryVectorStore;
    @Value("${rag.similarity.treshold}")
    private double similarityTreshold;

    public LongTermMemoryService(RedisTemplate<String, Object> redisTemplate,
                                 VectorStore longTermMemoryVectorStore) {
        this.redisTemplate = redisTemplate;
        this.longTermMemoryVectorStore = longTermMemoryVectorStore;
    }

    public boolean isDuplicate(String userId, String content) {
        return !this.searchLongTermMemory(userId, content).isEmpty();
    }

    public List<Document> searchLongTermMemory(String userId, String query) {
        return this.longTermMemoryVectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .similarityThreshold(this.similarityTreshold)
                        .topK(2)
                        .filterExpression("userId == '" + userId + "'")
                        .build());
    }

    @Override
    public void save(String userId, ExtractMemory memory) {
        if (userId == null || memory == null) return;

        if (isDuplicate(userId, memory.content())) {
            return;
        }

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("userId", userId);
        metadata.put("type", memory.type());
        Document document = new Document(memory.content(), metadata);

        this.longTermMemoryVectorStore.add(List.of(document));
    }

    @Override
    public List<Object> getMessages(String userId) {
        String key = PREFIX_KEY + userId;
        return this.redisTemplate.opsForList().range(key, 0, -1);
    }
}
