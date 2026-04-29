package org.abdev.rtfm.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.TokenCountBatchingStrategy;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.search.Schema;

@Configuration
public class VectorStoreConfig {

    @Bean
    public JedisPooled jedisPooled() {
        return new JedisPooled("localhost", 6379);
    }

    @Bean
    public VectorStore vectorStore(JedisPooled jedisPooled, EmbeddingModel embeddingModel) {
        return RedisVectorStore.builder(jedisPooled, embeddingModel)
                .indexName("docs-index")
                .prefix("docs:")
                .initializeSchema(true)
                .batchingStrategy(new TokenCountBatchingStrategy())
                .build();
    }

    @Bean("semanticCashingVectorSearch")
    public VectorStore semanticCachingVectorStore(JedisPooled jedisPooled, EmbeddingModel embeddingModel) {
        return RedisVectorStore.builder(jedisPooled, embeddingModel)
                .indexName("semantic-cashing")
                .prefix("semantic-cash:")
                .metadataFields(
                        new RedisVectorStore.MetadataField("answer", Schema.FieldType.TEXT)
                )
                .initializeSchema(true)
                .build();
    }

    @Bean("longTermMemoryVectorStore")
    public VectorStore longTermMemoryVectorStore(JedisPooled jedisPooled, EmbeddingModel embeddingModel) {
        return RedisVectorStore.builder(jedisPooled, embeddingModel)
                .indexName("long-term-memory")
                .prefix("ltm:")
                .metadataFields(
                        new RedisVectorStore.MetadataField("userId", Schema.FieldType.TEXT),
                        new RedisVectorStore.MetadataField("type", Schema.FieldType.TEXT)
                )
                .initializeSchema(true)
                .build();
    }
}
