package com.example.schedules;

import com.example.enumerate.schedules.DeleteType;
import com.example.enumerate.schedules.RepeatType;
import com.example.enumerate.schedules.RepeatUpdateType;
import com.example.enumerate.schedules.ScheduleType;
import com.example.events.enums.NotificationChannel;
import com.example.events.enums.ScheduleActionType;
import com.example.events.outbox.OutboxEventService;
import com.example.interfaces.notification.notification.NotificationInterfaces;
import com.example.model.schedules.SchedulesModel;
import com.example.outbound.schedule.NotificationChannelResolver;
import com.example.outbound.schedule.ScheduleOutConnector;
import com.example.security.config.SecurityUtil;
import com.example.service.schedule.domainService.ScheduleCreateService;
import com.example.service.schedule.domainService.ScheduleDeleteService;
import com.example.service.schedule.domainService.ScheduleDomainService;
import com.example.service.schedule.domainService.ScheduleQueryService;
import com.example.service.schedule.domainService.ScheduleUpdateService;
import com.example.service.schedule.domainService.guard.ScheduleGuard;
import com.example.service.schedule.domainService.repeat.create.RepeatScheduleFactory;
import com.example.service.schedule.domainService.repeat.delete.RepeatDeleteRegistry;
import com.example.service.schedule.domainService.repeat.update.RepeatUpdateRegistry;
import com.example.service.schedule.domainService.support.AttachBinder;
import com.example.service.schedule.domainService.support.DomainEventPublisher;
import com.example.service.schedule.domainService.support.ScheduleClassifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// ScheduleDomainService는 실제로는 4개 하위 도메인 서비스(Query/Create/Update/Delete)에
// 위임하는 구조라, 이들도 실제 빈으로 함께 로드해야 한다. 그 아래 협력자(out, guard 등)는
// 계속 @MockBean으로 대체된다.
@SpringBootTest(classes = {
        ScheduleDomainService.class,
        ScheduleQueryService.class,
        ScheduleCreateService.class,
        ScheduleUpdateService.class,
        ScheduleDeleteService.class
})
public class ScheduleCRUDTest {

    @Autowired
    ScheduleDomainService svc;
    @MockBean
    ScheduleOutConnector out;
    @MockBean
    NotificationChannelResolver notificationChannelResolver;
    @MockBean
    DomainEventPublisher events;
    @MockBean
    OutboxEventService outboxEventService;
    @MockBean
    AttachBinder attach;
    @MockBean
    NotificationInterfaces notificationInterfaces;
    @MockBean
    ScheduleGuard guard;
    @MockBean
    ScheduleClassifier classifier;
    @MockBean
    RepeatUpdateRegistry repeatUpdate;
    @MockBean
    RepeatDeleteRegistry repeatDelete;
    @MockBean
    RepeatScheduleFactory repeatCreate;

    @Test
    @DisplayName("일정 정상 생성")
    public void createScheduleSuccessTest() {
        try (MockedStatic<SecurityUtil> mocked = Mockito.mockStatic(SecurityUtil.class)) {
            // given
            mocked.when(SecurityUtil::currentUserId).thenReturn(100L);
            mocked.when(SecurityUtil::currentUserName).thenReturn("userA");

            LocalDateTime start = t(2025,8,20,9,0);
            LocalDateTime end = t(2025,8,20,10,0);
            //일정 요청
            SchedulesModel req = SchedulesModel
                    .builder()
                    .contents("회의")
                    .startTime(start)
                    .endTime(end)
                    .isAllDay(false)
                    .repeatType(RepeatType.NONE) .build();

            when(classifier.classify(any())).thenReturn(ScheduleType.SINGLE_DAY);
            // 벌크 충돌 체크(현재 구현은 findOverlappingSchedulesInRange를 사용)
            when(out.findOverlappingSchedulesInRange(anyLong(), any(), any())).thenReturn(0L);

            // save 결과 리턴
            SchedulesModel saved = req
                    .toBuilder()
                    .id(999L)
                    .memberId(100L)
                    .scheduleType(ScheduleType.SINGLE_DAY)
                    .build();

            // when
            when(notificationChannelResolver.resolveChannel(100L)).thenReturn(NotificationChannel.WEB);
            when(out.saveAll(anyList())).thenReturn(List.of(saved));
            SchedulesModel result = svc.saveSchedule(req);

            // then
            assertThat(result.getId()).isEqualTo(999L);
            assertThat(result.getMemberId()).isEqualTo(100L);
            assertThat(result.getScheduleType()).isEqualTo(ScheduleType.SINGLE_DAY);
            verify(out).saveAll(anyList());
            verify(attach, never()).bindToSchedule(anyList(), anyLong());
            // 실제 Outbox 저장은 도메인 이벤트를 구독하는 별도 리스너가 담당하므로,
            // 여기서는 이벤트 발행 자체(대상/타입)만 검증한다.
            verify(events).publish(anyList(), eq(ScheduleActionType.SCHEDULE_CREATED));
        }
    }

    @Test
    @DisplayName("일정 생성 실패-인증이 안된경우")
    public void createSchedule_fail_notAuthorization(){
        try (MockedStatic<SecurityUtil> mocked = Mockito.mockStatic(SecurityUtil.class)) {
            // SecurityUtil이 인증 예외를 던지도록
            mocked.when(SecurityUtil::currentUserId)
                    .thenThrow(new AccessDeniedException("인증 필요"));
            SchedulesModel req = baseSingle(
                    null,
                    "회의",
                    t(2025,8,20,9,0),
                    t(2025,8,20,10,0)
            );

            assertThatThrownBy(() -> svc.saveSchedule(req))
                    .isInstanceOf(org.springframework.security.access.AccessDeniedException.class)
                    .hasMessageContaining("인증"); }
    }

    @Test
    @DisplayName("일정 생성 성공-반복일정 생성")
    public void createSchedule_success_generateRepeatSchedule(){
        try (MockedStatic<SecurityUtil> mocked = Mockito.mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::currentUserId).thenReturn(100L);
            mocked.when(SecurityUtil::currentUserName).thenReturn("userA");
            // 요청: 주간 반복 3회
            SchedulesModel base = baseSingle(100L, "스터디",
                    t(2025,8,20,20,0),
                    t(2025,8,20,21,0))
                    .toBuilder()
                    .repeatType(RepeatType.WEEKLY)
                    .repeatCount(3)
                    .repeatInterval(1)
                    .build();
            // 팩토리가 3개 인스턴스 생성해서 준다고 가정
            List<SchedulesModel> generated = List
                    .of( base.toBuilder().repeatGroupId("G").build(),
                         base.toBuilder().repeatGroupId("G").build(),
                         base.toBuilder().repeatGroupId("G").build() );

            when(repeatCreate.generateRepeatedSchedules(base)).thenReturn(generated);

            // 분류 + 충돌검사 스텁
            when(classifier.classify(any())).thenReturn(ScheduleType.SINGLE_DAY);
            when(out.findOverlappingSchedulesInRange(anyLong(), any(), any())).thenReturn(0L);

            // saveAll이 한 번에 3건을 넘겨받아 id를 부여해 리턴 (개별 saveSchedule 호출이 아님)
            final long[] idSeq = new long[]{1};
            when(out.saveAll(anyList())).thenAnswer(inv -> {
                List<SchedulesModel> arg = inv.getArgument(0);
                return arg.stream()
                        .map(m -> m.toBuilder()
                                .id(idSeq[0]++)
                                .memberId(100L)
                                .scheduleType(ScheduleType.SINGLE_DAY)
                                .build())
                        .toList();
            });
            // when
            when(notificationChannelResolver.resolveChannel(100L)).thenReturn(NotificationChannel.WEB);
            SchedulesModel firstSaved = svc.saveSchedule(base);
            // then
            assertThat(firstSaved.getId()).isEqualTo(1L);
            assertThat(firstSaved.getMemberId()).isEqualTo(100L);
            // 3건이 한 번의 saveAll 호출로 저장됐는지 확인 (개별 saveSchedule 3회가 아님)
            verify(out, times(1)).saveAll(anyList());
            verify(events).publish(anyList(), eq(ScheduleActionType.SCHEDULE_CREATED));
        }
    }

    @Test
    @DisplayName("일정 생성 성공-하루종일 일정 설정")
    public void createSchedule_success_isAllDays(){
        try (MockedStatic<SecurityUtil> mocked = Mockito.mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::currentUserId).thenReturn(777L);
            LocalDateTime day = t(2025,8,30,0,0);
            SchedulesModel req = SchedulesModel.builder()
                    .contents("휴가")
                    .startTime(day)
                    .endTime(day)
                    // 같은 날
                    .isAllDay(true)
                    .repeatType(RepeatType.NONE)
                    .build();

            when(classifier.classify(any())).thenReturn(ScheduleType.ALL_DAY);
            when(out.findOverlappingSchedulesInRange(anyLong(), any(), any())).thenReturn(0L);
            when(out.saveAll(anyList())).thenAnswer(inv -> {
                List<SchedulesModel> arg = inv.getArgument(0);
                return List.of(arg.get(0).toBuilder()
                        .id(500L).memberId(777L).scheduleType(ScheduleType.ALL_DAY).build());
            });

            when(notificationChannelResolver.resolveChannel(777L)).thenReturn(NotificationChannel.WEB);
            SchedulesModel saved = svc.saveSchedule(req);

            assertThat(saved.getId()).isEqualTo(500L);
            assertThat(saved.getScheduleType()).isEqualTo(ScheduleType.ALL_DAY);
            assertThat(saved.getMemberId()).isEqualTo(777L);

        }
    }

    @Test
    @DisplayName("일정 단일 삭제 성공")
    public void deleteSchedule_success_Single(){
        try (MockedStatic<SecurityUtil> mocked = Mockito.mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::currentUserId).thenReturn(100L);
            LocalDateTime day = t(2025,8,30,0,0);
            SchedulesModel target = SchedulesModel
                    .builder()
                    .id(10L)
                    .startTime(day)
                    .endTime(day)
                    .memberId(100L)
                    .build();

            when(out.findById(10L)).thenReturn(target);
            doNothing().when(guard).assertOwnerOrAdmin(target);

            // 레지스트리는 SINGLE일 때 내부에서 out.deleteSchedule을 호출하고, 삭제 대상 목록을 리턴하도록 시뮬레이션
            doAnswer(inv -> { out.deleteSchedule(10L); return List.of(target); })
                    .when(repeatDelete).dispatch(eq(DeleteType.SINGLE), eq(target));
            // when
            when(notificationChannelResolver.resolveChannel(100L)).thenReturn(NotificationChannel.WEB);
            svc.deleteSchedule(10L, DeleteType.SINGLE);
            // then
            verify(repeatDelete).dispatch(DeleteType.SINGLE, target);
            verify(out).deleteSchedule(10L);
            verify(events).publish(anyList(), eq(ScheduleActionType.SCHEDULE_DELETE));
        }
    }

    @Test
    @DisplayName("일정 전체 삭제 성공")
    public void deleteSchedule_success_ALL_REPEAT(){
        try (MockedStatic<SecurityUtil> mocked = Mockito.mockStatic(SecurityUtil.class)) {
            // given
            mocked.when(SecurityUtil::currentUserId).thenReturn(200L);
            LocalDateTime day = t(2025,8,30,0,0);
            SchedulesModel target = SchedulesModel
                    .builder()
                    .id(20L)
                    .memberId(200L)
                    .startTime(day)
                    .repeatGroupId("RG")
                    .build();

            when(out.findById(20L)).thenReturn(target);
            doNothing().when(guard).assertOwnerOrAdmin(target);

            // 레지스트리가 내부에서 groupId 기준 일괄 삭제를 트리거한다고 가정
            doAnswer(inv -> { out.markAsDeletedByRepeatGroupId("RG"); return List.of(target); })
                    .when(repeatDelete).dispatch(eq(DeleteType.ALL_REPEAT), eq(target));

            // when
            when(notificationChannelResolver.resolveChannel(200L)).thenReturn(NotificationChannel.WEB);
            svc.deleteSchedule(20L, DeleteType.ALL_REPEAT);

            // then
            verify(repeatDelete).dispatch(DeleteType.ALL_REPEAT, target);
            verify(out).markAsDeletedByRepeatGroupId("RG");
            verify(events).publish(anyList(), eq(ScheduleActionType.SCHEDULE_DELETE));
        }
    }

    @Test
    @DisplayName("일정 일부 삭제 성공")
    public void deleteSchedule_success_AFTER_THIS(){
        try (MockedStatic<SecurityUtil> mocked = Mockito.mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::currentUserId).thenReturn(300L);
            LocalDateTime st = t(2025,9,1,9,0);
            SchedulesModel target = SchedulesModel
                    .builder()
                    .id(30L)
                    .memberId(300L)
                    .repeatGroupId("GRP")
                    .startTime(st)
                    .build();

            when(out.findById(30L)).thenReturn(target);
            doNothing().when(guard).assertOwnerOrAdmin(target);
            doAnswer(inv -> { out.markAsDeletedAfter("GRP", st); return List.of(target); })
                    .when(repeatDelete).dispatch(eq(DeleteType.AFTER_THIS), eq(target));
            when(notificationChannelResolver.resolveChannel(300L)).thenReturn(NotificationChannel.WEB);
            svc.deleteSchedule(30L, DeleteType.AFTER_THIS);

            verify(repeatDelete).dispatch(DeleteType.AFTER_THIS, target);
            verify(out).markAsDeletedAfter("GRP", st);
            verify(events).publish(anyList(), eq(ScheduleActionType.SCHEDULE_DELETE));
        }
    }

    @Test
    @DisplayName("일정 단일 수정 성공")
    public void updateSchedule_success_SINGLE(){
        try (MockedStatic<SecurityUtil> mocked = Mockito.mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::currentUserId).thenReturn(100L);
            SchedulesModel existing = SchedulesModel
                    .builder()
                    .id(1L)
                    .memberId(100L)
                    .contents("old")
                    .startTime(t(2025,8,22,9,0))
                    .endTime(t(2025,8,22,10,0))
                    .repeatType(RepeatType.NONE)
                    .build();

            when(out.findById(1L)).thenReturn(existing);
            doNothing().when(guard).assertOwnerOrAdmin(existing);

            SchedulesModel patch = existing.toBuilder().contents("new").build();
            SchedulesModel updated = existing.toBuilder().contents("new").build();

            when(repeatUpdate.dispatch(eq(RepeatUpdateType.SINGLE), eq(existing), eq(patch)))
                    .thenReturn(List.of(updated));
            when(notificationChannelResolver.resolveChannel(100L)).thenReturn(NotificationChannel.WEB);
            SchedulesModel result = svc.updateSchedule(1L, patch, RepeatUpdateType.SINGLE);

            assertThat(result.getContents()).isEqualTo("new");
            verify(repeatUpdate).dispatch(RepeatUpdateType.SINGLE, existing, patch);
            verify(events).publish(anyList(), eq(ScheduleActionType.SCHEDULE_UPDATE));
        }
    }

    @Test
    @DisplayName("반복일정 일부 수정 성공")
    public void updateSchedule_success_AFTER_THIS(){
        SchedulesModel existing = SchedulesModel
                .builder()
                .id(2L)
                .memberId(9L)
                .repeatGroupId("G")
                .startTime(t(2025,8,20,9,0))
                .endTime(t(2025,8,20,10,0))
                .repeatType(RepeatType.WEEKLY)
                .build();

        when(out.findById(2L)).thenReturn(existing);
        doNothing().when(guard).assertOwnerOrAdmin(existing);

        SchedulesModel patch = existing.toBuilder().contents("p").build();
        SchedulesModel updated = existing.toBuilder().contents("p").build();

        when(repeatUpdate.dispatch(eq(RepeatUpdateType.AFTER_THIS), eq(existing), eq(patch)))
                .thenReturn(List.of(updated));
        when(notificationChannelResolver.resolveChannel(9L)).thenReturn(NotificationChannel.WEB);
        SchedulesModel result = svc.updateSchedule(2L, patch, RepeatUpdateType.AFTER_THIS);

        assertThat(result.getContents()).isEqualTo("p");
        verify(repeatUpdate).dispatch(RepeatUpdateType.AFTER_THIS, existing, patch);
        verify(events).publish(anyList(), eq(ScheduleActionType.SCHEDULE_UPDATE));
    }

    @Test
    @DisplayName("반복일정 전체 수정 성공")
    public void updateSchedule_success_ALL_REPEAT(){
        SchedulesModel existing = SchedulesModel
                .builder()
                .id(3L)
                .memberId(9L)
                .repeatGroupId("G2")
                .startTime(t(2025,8,21,9,0))
                .endTime(t(2025,8,21,10,0))
                .repeatType(RepeatType.WEEKLY)
                .build();

        when(out.findById(3L)).thenReturn(existing);
        doNothing().when(guard).assertOwnerOrAdmin(existing);

        SchedulesModel patch = existing.toBuilder().contents("ALL").build();
        SchedulesModel updated = existing.toBuilder().contents("ALL").build();

        when(repeatUpdate.dispatch(eq(RepeatUpdateType.ALL), eq(existing), eq(patch)))
                .thenReturn(List.of(updated));
        when(notificationChannelResolver.resolveChannel(9L)).thenReturn(NotificationChannel.WEB);
        SchedulesModel result = svc.updateSchedule(3L, patch, RepeatUpdateType.ALL);

        assertThat(result.getContents()).isEqualTo("ALL");
        verify(repeatUpdate).dispatch(RepeatUpdateType.ALL, existing, patch);
    }

    private static LocalDateTime t(int y, int M, int d, int h, int m) {
        return LocalDateTime.of(y, M, d, h, m);
    }

    private static SchedulesModel baseSingle(Long userId, String contents, LocalDateTime start, LocalDateTime end) {
        return SchedulesModel
                .builder()
                .memberId(userId)
                .contents(contents)
                .startTime(start)
                .endTime(end)
                .isAllDay(false)
                .repeatType(RepeatType.NONE)
                .build();
    }
}
