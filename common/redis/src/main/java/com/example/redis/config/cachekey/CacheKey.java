package com.example.redis.config.cachekey;

import lombok.Getter;

@Getter
public enum CacheKey {
    USER("user"),
    REFRESH_TOKEN("refreshToken"),
    BLACKLIST("blackList"),
    CATEGORY("category"),
    SCHEDULE("schedule");

    private final String key;

    CacheKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static final String REFRESH_TOKEN_KEY = REFRESH_TOKEN.getKey();
}

