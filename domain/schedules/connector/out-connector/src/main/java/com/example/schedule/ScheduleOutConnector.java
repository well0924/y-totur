package com.example.schedule;

import com.example.enumerate.schedules.PROGRESS_STATUS;
import com.example.model.schedules.SchedulesModel;
import com.example.rdb.member.MemberRepository;
import com.example.rdbrepository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ScheduleOutConnector {

    private final ScheduleRepository scheduleRepository;

    private final MemberRepository memberRepository;

    private final CategoryRepository categoryRepository;

    public List<SchedulesModel> findAll() {
        return scheduleRepository.findAll().stream().map(this::toModel).collect(Collectors.toList());
    }

    public SchedulesModel findById(Long scheduleId) {
        Schedules schedules = scheduleRepository.findById(scheduleId).orElseThrow();
        return toModel(schedules);
    }

    //일정저장
    public SchedulesModel saveSchedule(SchedulesModel model) {
        validateScheduleData(model);

        Schedules schedules = Schedules
                .builder()
                .contents(model.getContents())
                .scheduleDay(model.getScheduleDays())
                .scheduleMonth(model.getScheduleMonth())
                .startTime(model.getStartTime())
                .endTime(model.getEndTime())
                .userId(model.getUserId())
                .categoryId(model.getCategoryId())
                .PROGRESS_STATUS(PROGRESS_STATUS.IN_COMPLETE)
                .build();

        return toModel(scheduleRepository.save(schedules));
    }

    //일정 수정
    public SchedulesModel updateSchedule(Long scheduleId, SchedulesModel model) {
        Schedules schedules = getScheduleById(scheduleId);

        validateScheduleData(model);

        Schedules updateSchedules = schedules
                .toBuilder()
                .contents(model.getContents())
                .scheduleDay(model.getScheduleDays())
                .scheduleMonth(model.getScheduleMonth())
                .PROGRESS_STATUS(model.getProgressStatus())
                .startTime(model.getStartTime())
                .endTime(model.getEndTime())
                .categoryId(model.getCategoryId())
                .userId(model.getUserId())
                .build();

        return toModel(scheduleRepository.save(updateSchedules));
    }

    //일정 삭제
    public void deleteSchedule(Long scheduleId) {
        scheduleRepository.delete(getScheduleById(scheduleId));
    }

    public List<SchedulesModel> findByUserId(Long userId) {
        return scheduleRepository.findByUserId(userId)
                .stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    public List<SchedulesModel> findByCategoryId(Long categoryId) {
        return scheduleRepository.findByCategoryId(categoryId)
                .stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    public void updateScheduleStatus(Long scheduleId) {
        Schedules schedules = getScheduleById(scheduleId);

        LocalDateTime now = LocalDateTime.now();
        // 기존 상태를 기반으로 새로운 상태를 계산
        PROGRESS_STATUS newStatus;
        if (schedules.getEndTime().isBefore(now)) {
            newStatus = PROGRESS_STATUS.COMPLETE;
        } else if (schedules.getStartTime().isBefore(now)) {
            newStatus = PROGRESS_STATUS.PROGRESS;
        } else {
            newStatus = PROGRESS_STATUS.IN_COMPLETE;
        }

        // Builder를 사용하여 상태를 변경한 새로운 객체를 생성
        Schedules updatedSchedule = schedules
                .toBuilder()
                .PROGRESS_STATUS(newStatus)
                .build();

        scheduleRepository.save(updatedSchedule);
    }

    private void validateScheduleData(SchedulesModel model) {
        if (!memberRepository.existsById(model.getUserId())) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        if (!categoryRepository.existsById(model.getCategoryId())) {
            throw new IllegalArgumentException("존재하지 않는 카테고리입니다.");
        }
        if (model.getStartTime().isAfter(model.getEndTime())) {
            throw new IllegalArgumentException("시작 시간은 종료 시간보다 이후일 수 없습니다.");
        }
    }

    private Schedules getScheduleById(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 일정입니다."));
    }

    private SchedulesModel toModel(Schedules schedules) {
        return SchedulesModel
                .builder()
                .id(schedules.getId())
                .contents(schedules.getContents())
                .scheduleDays(schedules.getScheduleDay())
                .scheduleMonth(schedules.getScheduleMonth())
                .progressStatus(schedules.getPROGRESS_STATUS())
                .startTime(schedules.getStartTime())
                .endTime(schedules.getEndTime())
                .categoryId(schedules.getCategoryId())
                .userId(schedules.getUserId())
                .build();
    }
}
