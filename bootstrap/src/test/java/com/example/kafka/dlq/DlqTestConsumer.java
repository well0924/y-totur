package com.example.kafka.dlq;

import com.example.events.kafka.MemberSignUpKafkaEvent;
import com.example.events.kafka.NotificationEvents;
import com.example.notification.model.FailMessageModel;
import com.example.notification.service.FailedMessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// "test" 프로파일이 활성화된 모든 테스트에서 이 빈이 뜨는데, 여기서 참조하는
// testMemberKafkaListenerFactory/testNotificationKafkaListenerFactory는
// KafkaIntegrationTest의 @TestConfiguration에서만 정의되므로, 다른 전체-컨텍스트
// 테스트(BootStrapApplicationTest, AttachIntegrateTest 등)에서는 빈 생성이 실패했다.
// kafka-dlq-test 프로파일과 AND 조건으로 좁혀서 KafkaIntegrationTest에서만 활성화한다.
@Profile("test & kafka-dlq-test")
@Component
@AllArgsConstructor
public class DlqTestConsumer {

    private final List<MemberSignUpKafkaEvent> MemberDlqMessages = new ArrayList<>();

    private final List<NotificationEvents> NotificationDlqMessage = new ArrayList<>();

    private final FailedMessageService failedMessageService;

    private final ObjectMapper objectMapper; // 직렬화용


    @KafkaListener(topics = "member-signup-events.DLQ",
            groupId ="test-member-signup-group",
            containerFactory = "testMemberKafkaListenerFactory")
    public void consumeDlq(MemberSignUpKafkaEvent event) {

        MemberDlqMessages.add(event);

        try {
            // JSON 직렬화
            String payload = objectMapper.writeValueAsString(event);

            // 실패 이력 저장
            failedMessageService.createFailMessage(FailMessageModel.builder()
                    .topic("member-signup-events")
                    .messageType("MEMBER_SIGNUP")
                    .exceptionMessage("테스트 실패 시뮬레이션")
                    .payload(payload)
                    .eventId(event.getEventId())
                    .resolved(false)
                    .retryCount(0)
                    .createdAt(LocalDateTime.now())
                    .build());

        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new RuntimeException("DLQ 테스트용 직렬화 실패", e);
        }
    }

    @KafkaListener(topics = "notification-events.DLQ",
            groupId = "test-notification-group",
            containerFactory = "testNotificationKafkaListenerFactory")
    public void consumeDlq(NotificationEvents event) {

        NotificationDlqMessage.add(event);

        try {
            // JSON 직렬화
            String payload = objectMapper.writeValueAsString(event);

            // 실패 이력 저장
            failedMessageService.createFailMessage(FailMessageModel.builder()
                    .topic("notification-events")
                    .messageType("NOTIFICATION")
                    .exceptionMessage("테스트 실패 시뮬레이션")
                    .payload(payload)
                    .eventId(event.getEventId())
                    .resolved(false)
                    .retryCount(0)
                    .createdAt(LocalDateTime.now())
                    .build());

        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new RuntimeException("DLQ 테스트용 직렬화 실패", e);
        }
    }

    public List<MemberSignUpKafkaEvent> getMemberDlqMessages() {
        return MemberDlqMessages;
    }

    public List<NotificationEvents> getNotificationDlqMessages() {
        return NotificationDlqMessage;
    }

    public void clear() {
        MemberDlqMessages.clear();
        NotificationDlqMessage.clear();
    }
}
