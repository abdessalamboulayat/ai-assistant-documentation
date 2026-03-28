package org.abdev.rtfm.service.impl;

import org.abdev.rtfm.dto.ChatMessage;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SessionMemoryService {

    private final static String PREFIX_KEY="spring-session:";

    private final RedisTemplate<String, Object> redisTemplate;

    public SessionMemoryService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void addMessage(String sessionId, ChatMessage chatMessage) {
        String key = PREFIX_KEY + sessionId;
        redisTemplate.opsForList().rightPush(key, chatMessage);
    }

    public List<Object> getMessages(String sessionId) {
        String key = PREFIX_KEY + sessionId;
        return redisTemplate.opsForList().range(key, 0, -1);
    }
}
