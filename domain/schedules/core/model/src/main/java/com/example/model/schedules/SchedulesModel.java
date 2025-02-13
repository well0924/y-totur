package com.example.model.schedules;

import com.example.enumerate.schedules.PROGRESS_STATUS;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchedulesModel {

    private Long id;
    private String contents;
    private int scheduleDays;
    private int scheduleMonth;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long userId; //회원 번호
    private Long categoryId; // 카테고리 번호
    private PROGRESS_STATUS progressStatus;

    private String createdBy;
    private LocalDateTime createdTime;
    private String updatedBy;
    private LocalDateTime updatedTime;

    //진행상태 변경
    public void updateProgressStatus() {
        if (this.startTime != null && LocalDateTime.now().isAfter(this.startTime)) {
            this.progressStatus = PROGRESS_STATUS.PROGRESS;
        }
        if (this.endTime != null && LocalDateTime.now().isAfter(this.endTime)) {
            this.progressStatus = PROGRESS_STATUS.COMPLETE;
        }
    }

    //진행상태 수정
    public void updateSchedule(LocalDateTime newStartTime, LocalDateTime newEndTime, String updatedBy) {
        if (this.progressStatus == PROGRESS_STATUS.COMPLETE) {
            throw new IllegalStateException("완료된 일정은 수정할 수 없습니다.");
        }
        if (newStartTime != null && newEndTime != null && newStartTime.isAfter(newEndTime)) {
            throw new IllegalArgumentException("시작 시간은 종료 시간보다 이후일 수 없습니다.");
        }

        this.startTime = newStartTime;
        this.endTime = newEndTime;
        this.updatedBy = updatedBy;
        this.updatedTime = LocalDateTime.now();

        updateProgressStatus();  // 상태 업데이트
    }

    //진행완료변경
    public void markAsComplete() {
        if (this.progressStatus == PROGRESS_STATUS.COMPLETE) {
            throw new IllegalStateException("이미 완료된 일정입니다.");
        }

        this.progressStatus = PROGRESS_STATUS.COMPLETE;
        this.endTime = LocalDateTime.now();
    }

    //특정 상태의 일정을 조회
    public static List<SchedulesModel> filterByStatus(List<SchedulesModel> schedules, PROGRESS_STATUS status) {
        return schedules.stream()
                .filter(schedule -> schedule.getProgressStatus() == status)
                .collect(Collectors.toList());
    }
    
    //특정 사용자의 일정을 조회
    public static List<SchedulesModel> getSchedulesByUser(List<SchedulesModel> schedules, Long userId) {
        return schedules.stream()
                .filter(schedule -> schedule.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    //특정 카테고리의 일정을 조회
    public static List<SchedulesModel> getSchedulesByCategory(List<SchedulesModel> schedules, Long categoryId) {
        return schedules.stream()
                .filter(schedule -> schedule.getCategoryId().equals(categoryId))
                .collect(Collectors.toList());
    }

    public boolean isScheduleStarted() {
        return this.startTime != null && LocalDateTime.now().isAfter(this.startTime);
    }

    public boolean isScheduleEnded() {
        return this.endTime != null && LocalDateTime.now().isAfter(this.endTime);
    }

    public Duration getTimeUntilStart() {
        if (this.startTime == null || isScheduleStarted()) {
            return Duration.ZERO; // 이미 시작된 경우 0 반환
        }
        return Duration.between(LocalDateTime.now(), this.startTime);
    }

    public Duration getElapsedTime() {
        if (!isScheduleStarted()) {
            return Duration.ZERO;
        }
        return Duration.between(this.startTime, LocalDateTime.now());
    }

    public Duration getTimeUntilEnd() {
        if (this.endTime == null || isScheduleEnded()) {
            return Duration.ZERO; // 이미 종료된 경우 0 반환
        }
        return Duration.between(LocalDateTime.now(), this.endTime);
    }

    //일정 총 시간을 계산
    public Duration getTotalDuration() {
        if (this.startTime == null || this.endTime == null) {
            return Duration.ZERO;
        }
        return Duration.between(this.startTime, this.endTime);
    }
}
