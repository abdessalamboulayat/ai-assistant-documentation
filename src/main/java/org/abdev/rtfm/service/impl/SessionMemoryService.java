package org.abdev.rtfm.service.impl;

import org.abdev.rtfm.dto.ChatMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class SessionMemoryService {

    private final static String PREFIX_KEY="spring-session:";

    private final long sessionChatTtl;
    private final RedisTemplate<String, Object> redisTemplate;

    public SessionMemoryService(RedisTemplate<String, Object> redisTemplate,
                                @Value("${redis.session.chat.ttl}") long sessionChatTtl) {
        this.redisTemplate = redisTemplate;
        this.sessionChatTtl = sessionChatTtl;
    }

    public void addMessage(String sessionId, ChatMessage chatMessage) {
        String key = this.buildKeySession(sessionId);
        redisTemplate.opsForList().rightPush(key, chatMessage);
        Long ttl = redisTemplate.getExpire(key);
        if (ttl == null || ttl == -1) {
            redisTemplate.expire(key, Duration.ofHours(sessionChatTtl));
        }
    }

    private String buildKeySession(String sessionId) {
        return PREFIX_KEY + sessionId;
    }

    public List<Object> getMessages(String sessionId) {
        String key = PREFIX_KEY + sessionId;
        return redisTemplate.opsForList().range(key, 0, -1);
    }
}
