package com.example.service.auth;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public void save(String key, String token, long expirationTime) {
        redisTemplate.opsForValue().set(key, token, expirationTime, TimeUnit.MILLISECONDS);
    }

    public String findByKey(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
