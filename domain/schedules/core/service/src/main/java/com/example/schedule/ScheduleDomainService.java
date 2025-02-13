package com.example.schedule;

import com.example.enumerate.schedules.PROGRESS_STATUS;
import com.example.model.attach.AttachModel;
import com.example.model.schedules.SchedulesModel;
import com.example.service.attach.AttachService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class ScheduleDomainService {

    private final ScheduleOutConnector scheduleOutConnector;

    private final AttachService attachService;

    public List<SchedulesModel> getAllSchedules() {
        return scheduleOutConnector.findAll();
    }

    public List<SchedulesModel> getSchedulesByUser(Long userId) {
        return scheduleOutConnector.findByUserId(userId);
    }

    public List<SchedulesModel> getSchedulesByCategory(Long categoryId) {
        return scheduleOutConnector.findByCategoryId(categoryId);
    }

    public SchedulesModel saveSchedule(SchedulesModel model, List<MultipartFile> files) throws IOException {

        //첨부파일 추가.
        if (files != null && !files.isEmpty()) {
            List<AttachModel> attachModels = attachService.createAttach(files);
        }

        SchedulesModel schedulesModel = scheduleOutConnector.saveSchedule(model);

        return schedulesModel;
    }

    public SchedulesModel updateSchedule(Long scheduleId, SchedulesModel model, List<MultipartFile> files) throws IOException {

        SchedulesModel schedule = scheduleOutConnector.findById(scheduleId);

        if (schedule.getProgressStatus() == PROGRESS_STATUS.COMPLETE) {
            throw new IllegalStateException("완료된 일정은 수정할 수 없습니다.");
        }
        //첨부파일 수정
        schedule.updateSchedule(model.getStartTime(), model.getEndTime(), model.getUpdatedBy());

        if (files != null && !files.isEmpty()) {
            // 기존 파일 삭제
            List<AttachModel> existingFiles = attachService.findAll(); // 이 부분을 적절한 방식으로 수정
            for (AttachModel file : existingFiles) {
                attachService.deleteAttach(file.getId());
            }

            // 새로운 파일 저장
            List<AttachModel> newAttachModels = attachService.createAttach(files);
        }

        return scheduleOutConnector.updateSchedule(scheduleId, schedule);
    }

    public void deleteSchedule(Long scheduleId) {
        scheduleOutConnector.deleteSchedule(scheduleId);
    }

    public void updateProgressStatus(Long scheduleId) {
        SchedulesModel schedule = scheduleOutConnector.findById(scheduleId);
        schedule.updateProgressStatus();
        scheduleOutConnector.updateSchedule(scheduleId, schedule);
    }

    public void markScheduleAsComplete(Long scheduleId) {
        SchedulesModel schedule = scheduleOutConnector.findById(scheduleId);
        schedule.markAsComplete();
        scheduleOutConnector.updateSchedule(scheduleId, schedule);
    }

    public Duration getTimeUntilStart(Long scheduleId) {
        SchedulesModel schedule = scheduleOutConnector.findById(scheduleId);
        return schedule.getTimeUntilStart();
    }

    public Duration getElapsedTime(Long scheduleId) {
        SchedulesModel schedule = scheduleOutConnector.findById(scheduleId);
        return schedule.getElapsedTime();
    }

    public Duration getTimeUntilEnd(Long scheduleId) {
        SchedulesModel schedule = scheduleOutConnector.findById(scheduleId);
        return schedule.getTimeUntilEnd();
    }

    public Duration getTotalDuration(Long scheduleId) {
        SchedulesModel schedule = scheduleOutConnector.findById(scheduleId);
        return schedule.getTotalDuration();
    }

    public Duration getTotalDurationForAllSchedules(Long scheduleId) {
        SchedulesModel schedule = scheduleOutConnector.findById(scheduleId);
        return schedule.getTotalDuration();
    }

    public List<SchedulesModel> getSchedulesByStatus(PROGRESS_STATUS status) {
        List<SchedulesModel> allSchedules = scheduleOutConnector.findAll();
        return SchedulesModel.filterByStatus(allSchedules, status);
    }

    public List<SchedulesModel> getSchedulesByUserFilter(Long userId) {
        List<SchedulesModel> allSchedules = scheduleOutConnector.findAll();
        return SchedulesModel.getSchedulesByUser(allSchedules, userId);
    }

    public List<SchedulesModel> getSchedulesByCategoryFilter(Long categoryId) {
        List<SchedulesModel> allSchedules = scheduleOutConnector.findAll();
        return SchedulesModel.getSchedulesByCategory(allSchedules, categoryId);
    }
}
