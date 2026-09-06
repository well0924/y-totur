package com.example.chatbot;

import com.example.events.process.ProcessedEventService;
import com.example.events.spring.ChatCompletedEvent;
import com.example.inbound.consumer.chatbot.PatternAnalysisConsume;
import com.example.inbound.schedules.ScheduleRecommendationCachePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * PatternAnalysisConsume.handle()은 두 가지를 한다:
 * 1) analyzeTimePreference: 메시지 내용과 무관하게 event.createdAt()의 "시각"으로
 *    morning/afternoon을 나눠 "pattern:time:{memberId}" 키에 increment
 * 2) analyzeMessageContext: 메시지 키워드(운동/헬스 -> health, 공부/독서 -> study)로
 *    "pattern:interest:{memberId}" 키에 increment
 * 기존 테스트는 존재하지 않는 cachePort.set()을 검증하고, 시간대 판정도 메시지 키워드로
 * 오해하고 있어 실제 구현과 맞지 않았다.
 */
@ExtendWith(MockitoExtension.class)
public class PatternAnalysisConsumerTest {

    @Mock
    private ScheduleRecommendationCachePort cachePort;

    @Mock
    private Acknowledgment ack;

    // handle()이 제일 먼저 호출하는 의존성인데 기존 테스트엔 없어서 NPE가 나던 부분
    @Mock
    private ProcessedEventService processedEventService;

    @InjectMocks
    private PatternAnalysisConsume consumer;

    @BeforeEach
    void setUp() {
        when(processedEventService.isAlreadyProcessed(anyString())).thenReturn(false);
    }

    @Test
    @DisplayName("오전 시간대 채팅 - time 패턴에 morning 누적")
    void handle_morningHour_incrementsMorningPattern() {
        // given
        ChatCompletedEvent event = ChatCompletedEvent.builder()
                .memberId(1L)
                .userMessage("일정 보여줘")
                .assistantResponse("답변")
                .createdAt(LocalDateTime.of(2025, 1, 1, 9, 0))
                .build();

        // when
        consumer.handle(event, ack);

        // then
        verify(cachePort).increment(eq("pattern:time:1"), eq("morning"), eq(1L));
        verify(ack).acknowledge();
    }

    @Test
    @DisplayName("오후 시간대 채팅 - time 패턴에 afternoon 누적")
    void handle_afternoonHour_incrementsAfternoonPattern() {
        // given
        ChatCompletedEvent event = ChatCompletedEvent.builder()
                .memberId(1L)
                .userMessage("일정 보여줘")
                .assistantResponse("답변")
                .createdAt(LocalDateTime.of(2025, 1, 1, 15, 0))
                .build();

        // when
        consumer.handle(event, ack);

        // then
        verify(cachePort).increment(eq("pattern:time:1"), eq("afternoon"), eq(1L));
        verify(ack).acknowledge();
    }

    @Test
    @DisplayName("운동 키워드 포함 - interest 패턴에 health 누적")
    void handle_exerciseKeyword_incrementsHealthPattern() {
        // given
        ChatCompletedEvent event = ChatCompletedEvent.builder()
                .memberId(1L)
                .userMessage("운동 일정 잡아줘")
                .assistantResponse("답변")
                .createdAt(LocalDateTime.of(2025, 1, 1, 9, 0))
                .build();

        // when
        consumer.handle(event, ack);

        // then
        verify(cachePort).increment(eq("pattern:interest:1"), eq("health"), eq(1L));
        verify(ack).acknowledge();
    }

    @Test
    @DisplayName("관련 키워드 없을 때 - interest 패턴은 저장 안 됨")
    void handle_noKeyword_noInterestPatternSaved() {
        // given
        ChatCompletedEvent event = ChatCompletedEvent.builder()
                .memberId(1L)
                .userMessage("일정 보여줘")
                .assistantResponse("답변")
                .createdAt(LocalDateTime.of(2025, 1, 1, 9, 0))
                .build();

        // when
        consumer.handle(event, ack);

        // then: 시간대 패턴은 메시지 내용과 무관하게 항상 누적되지만,
        // interest 패턴은 키워드가 없으면 호출되지 않는다.
        verify(cachePort, never()).increment(eq("pattern:interest:1"), any(), anyLong());
        verify(ack).acknowledge();
    }
}
