package com.example.schedule;

import com.example.enumerate.schedules.PROGRESS_STATUS;
import com.example.exception.schedules.dto.ScheduleErrorCode;
import com.example.exception.schedules.exception.ScheduleCustomException;
import com.example.inconnector.attach.AttachInConnector;
import com.example.model.schedules.SchedulesModel;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class ScheduleDomainService {

    private final ScheduleOutConnector scheduleOutConnector;

    private final AttachInConnector attachInConnector;

    @Transactional(readOnly = true)
    public List<SchedulesModel> getAllSchedules() {
        return scheduleOutConnector.findAllSchedules();
    }

    @Transactional(readOnly = true)
    public List<SchedulesModel> getAllDeletedSchedules() {
        return scheduleOutConnector.findAllByIsDeletedScheduled();
    }

    //회원별 일정목록
    @Transactional(readOnly = true)
    public Page<SchedulesModel> getSchedulesByUserFilter(String userId, Pageable pageable) {
        return scheduleOutConnector.findByUserId(userId,pageable);
    }

    //카테고리별 일정목록
    @Transactional(readOnly = true)
    public Page<SchedulesModel> getSchedulesByCategoryFilter(String categoryId,Pageable pageable) {
        return scheduleOutConnector.findByCategoryId(categoryId,pageable);
    }

    //일정상태별 일정목록
    @Transactional(readOnly = true)
    public Page<SchedulesModel> getSchedulesByStatus(String status,String userId,Pageable pageable) {
        return scheduleOutConnector.findAllByPROGRESS_STATUS(userId,status,pageable);
    }

    //일정 단일 조회
    @Transactional(readOnly = true)
    public SchedulesModel findById(Long scheduleId) {
        return scheduleOutConnector.findById(scheduleId);
    }

    //일정 등록
    public SchedulesModel saveSchedule(SchedulesModel model) {

        // 일정 등록
        SchedulesModel savedSchedule = scheduleOutConnector.saveSchedule(model);

        if (savedSchedule == null || savedSchedule.getId() == null) {
            throw new ScheduleCustomException(ScheduleErrorCode.SCHEDULE_CREATED_FAIL);
        }

        // 첨부파일이 있는 경우 일정 ID를 업데이트
        if (model.getAttachIds() != null && !model.getAttachIds().isEmpty()) {
            attachInConnector.updateScheduleId(model.getAttachIds(), savedSchedule.getId());
        }

        return savedSchedule;
    }

    //일정 수정
    public SchedulesModel updateSchedule(Long scheduleId, SchedulesModel model, List<MultipartFile> files) throws IOException {

        SchedulesModel schedule = scheduleOutConnector.findById(scheduleId);

        if (schedule.getProgressStatus() == PROGRESS_STATUS.COMPLETE) {
            throw new ScheduleCustomException(ScheduleErrorCode.SCHEDULE_COMPLETED);
        }
        //일정 수정.
        schedule.updateSchedule(model.getStartTime(), model.getEndTime());
        //일정 상태 수정
        updateProgressStatus(model.getId());
        //특정 조건(현재 시간이 일정 종료 시간 이후)이 되면 완료로 처리하기.
        if (schedule.getProgressStatus() == PROGRESS_STATUS.COMPLETE) {
            markScheduleAsComplete(scheduleId);
        }
        //첨부파일이 있는 경우 수정
        if (files != null && !files.isEmpty()) {
            attachInConnector.updateAttach(files,scheduleId);
        }

        return scheduleOutConnector.updateSchedule(scheduleId, schedule);
    }
    //일정 삭제 (논리 삭제)
    public void deleteSchedule(Long scheduleId) {
        scheduleOutConnector.deleteSchedule(scheduleId);
    }

    //일괄삭제 기능 (자정마다 작동이 되게끔 하기)
    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteOldSchedules() {
        LocalDateTime thresholdDate = LocalDateTime.now().minusMonths(1);
        int deletedCount = scheduleOutConnector.deleteOldSchedules(thresholdDate);
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

}
