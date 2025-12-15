package com.talet.talet.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisUtil {
    private final RedisTemplate<String, String> redisTemplate;

    public void saveToken(RedisTokenType type, String key, String token) {
        redisTemplate.opsForValue().set(
                type.getKey(key),
                token,
                type.getDuration()
        );
    }


    public String getToken(RedisTokenType type, String key) {
        return redisTemplate.opsForValue().get(type.getKey(key));
    }

    public void deleteToken(RedisTokenType type, String key) {
        redisTemplate.delete(type.getKey(key));
    }

    public boolean hasToken(RedisTokenType type, String key) {
        return redisTemplate.hasKey(type.getKey(key));
    }

    public boolean validateToken(RedisTokenType type, String key, String token) {
        String stored = getToken(type, key);
        return stored != null && stored.equals(token);
    }

}
