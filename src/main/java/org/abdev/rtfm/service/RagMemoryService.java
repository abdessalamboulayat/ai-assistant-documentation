package org.abdev.rtfm.service;

import org.abdev.rtfm.dto.ExtractMemory;

import java.util.List;

public interface RagMemoryService {

    void save(String userId, ExtractMemory content);

    List<Object> getMessages(String userId);

    default String buildKeySession(String prefixKey, String sessionId) {
        return prefixKey + sessionId;
    }
}
