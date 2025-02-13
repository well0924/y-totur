package com.example.schedules;

import org.springframework.web.multipart.MultipartFile;

import static com.example.apimodel.ScheduleApiModel.responseSchedule;
import static com.example.apimodel.ScheduleApiModel.requestSchedule;
import static com.example.apimodel.ScheduleApiModel.updateSchedule;
import java.util.List;

public interface ScheduleServiceConnector {

    List<responseSchedule> findAll();

    List<responseSchedule> getSchedulesByUserId(Long userId);

    List<responseSchedule> getSchedulesByCategoryId(Long categoryId);

    responseSchedule saveSchedule(requestSchedule requestSchedule, List<MultipartFile>files);

    responseSchedule updateSchedule(updateSchedule updateSchedule, List<MultipartFile>files);

    void deleteSchedule(Long scheduleId);

    List<responseSchedule> getSchedulesByStatus();
}
