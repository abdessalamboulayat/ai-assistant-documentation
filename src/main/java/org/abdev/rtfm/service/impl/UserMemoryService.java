package org.abdev.rtfm.service.impl;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserMemoryService {

    private final RedisTemplate<String, Object> redisTemplate;

    public UserMemoryService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
}
