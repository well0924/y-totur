package com.example.outbound.schedule;

import com.example.events.enums.AggregateType;
import com.example.events.enums.NotificationChannel;
import com.example.events.kafka.NotificationEvents;
import com.example.events.outbox.OutboxEventService;
import com.example.events.spring.ScheduleDomainEvent;
import com.example.events.spring.ScheduleEvents;
import com.example.model.schedules.SchedulesModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduleEventListener {

    private final NotificationChannelResolver notificationChannelResolver;
    private final OutboxEventService outboxEventService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleScheduleDomainEvent(ScheduleDomainEvent event) {
        log.info("[Outbox 적재 시작] 수신된 도메인 이벤트 Action: {}, 대상 건수: {}건",
                event.actionType(), event.schedules().size());

        List<SchedulesModel> targets = event.schedules();
        List<Object> eventDtos = new ArrayList<>();
        List<String> aggregateIds = new ArrayList<>();
        List<String> eventTypes = new ArrayList<>();

        // 대량삭제/반복일정 수정 등으로 targets에 여러 건이 들어와도, 소유권 검증(assertOwnerOrAdmin/
        // findOwnedIds)을 거치기 때문에 항상 같은 memberId다. 매번 다시 조회하지 않도록 요청 1건
        // 안에서만 memberId별로 1회 캐싱한다.
        Map<Long, NotificationChannel> channelByMember = new HashMap<>();

        for (SchedulesModel model : targets) {
            // 1. 유저별 알림 채널 동적 결정 (웹알림 우선 혹은 푸시 우선)
            NotificationChannel channel = channelByMember.computeIfAbsent(
                    model.getMemberId(), notificationChannelResolver::resolveChannel);

            // 2. 외부 카프카로 전송될 공통 Notification 구조체 래핑
            NotificationEvents kafkaEvent = NotificationEvents.of(ScheduleEvents.builder()
                    .scheduleId(model.getId())
                    .startTime(model.getStartTime())
                    .contents(model.getContents())
                    .userId(model.getMemberId())
                    .notificationChannel(channel)
                    .notificationType(event.actionType())
                    .createdTime(model.getCreatedTime())
                    .build());

            eventDtos.add(kafkaEvent);
            aggregateIds.add(model.getId().toString());
            eventTypes.add(event.actionType().name());
        }

        // 3. Outbox 서비스 호출하여 하나의 쿼리로 벌크 인서트
        if (!eventDtos.isEmpty()) {
            outboxEventService.saveAllEvents(
                    eventDtos,
                    AggregateType.SCHEDULE.name(),
                    aggregateIds,
                    eventTypes
            );
            log.info("[Outbox 적재 완료] 동일 트랜잭션 내 Outbox 데이터 세팅 완료");
        }
    }

}
