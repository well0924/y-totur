package com.example.inbound.schedules;

import com.example.outbound.openai.dto.ChatMessage;
import com.fasterxml.jackson.core.type.TypeReference;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ScheduleRecommendationCachePort {

    <T> void set(String key, T value, Duration ttl);
    <T> Optional<T> get(String key, TypeReference<T> typeRef);
    void appendChatMessage(Long memberId, ChatMessage message);
    List<ChatMessage> getChatHistory(Long memberId);
    void clearChatHistory(Long memberId);
    public void increment(String key, String hashKey, long delta);
    // increment()로 누적한 해시(예: pattern:time:*, pattern:interest:*)를 읽어온다.
    Map<String, Long> getHash(String key);
}
