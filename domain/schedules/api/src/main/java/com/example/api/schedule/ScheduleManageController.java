package com.example.api.schedule;

import com.example.apimodel.ScheduleApiModel;
import com.example.enumerate.schedules.DeleteType;
import com.example.schedules.ScheduleServiceConnectorImpl;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/schedule")
public class ScheduleManageController {

    private final ScheduleServiceConnectorImpl scheduleServiceConnector;

    @GetMapping("/")
    public List<ScheduleApiModel.responseSchedule> findAll() {
        return scheduleServiceConnector.findAll();
    }

    @GetMapping("/deleted")
    public List<ScheduleApiModel.responseSchedule> deletedSchedulesAll() {
        return scheduleServiceConnector.findAllDeletedSchedules();
    }

    @GetMapping("/user/{id}")
    public Page<ScheduleApiModel.responseSchedule> findAllByUserId(@PathVariable("id")String userId,@PageableDefault Pageable pageable) {
        return scheduleServiceConnector.getSchedulesByUserId(userId,pageable);
    }

    @GetMapping("/category/{category-name}")
    public Page<ScheduleApiModel.responseSchedule> findAllByCategoryId(@PathVariable("category-name")String categoryName,Pageable pageable) {
        return scheduleServiceConnector.getSchedulesByCategoryName(categoryName,pageable);
    }

    @GetMapping("/status")
    public Page<ScheduleApiModel.responseSchedule> findAllByPRGRESS_STATUS(@RequestParam("status") String progressStatus,
                                                                           @RequestParam("userId") String userId,
                                                                           @PageableDefault(sort = "id",direction = Sort.Direction.DESC) Pageable pageable) {
        return scheduleServiceConnector.getSchedulesByStatus(progressStatus,userId,pageable);
    }

    @GetMapping("/{id}")
    public ScheduleApiModel.responseSchedule findById(@PathVariable("id")Long scheduleId) {
        System.out.println(scheduleId);
        return scheduleServiceConnector.findById(scheduleId);
    }

    @PostMapping("/")
    public ScheduleApiModel.responseSchedule createSchedule(@Validated @RequestBody ScheduleApiModel.requestSchedule requestSchedule) throws IOException {
        return scheduleServiceConnector.saveSchedule(requestSchedule);
    }

    @PatchMapping("/{id}")
    public ScheduleApiModel.responseSchedule updateSchedule(@PathVariable("id")Long scheduleId,@Validated @RequestBody ScheduleApiModel.updateSchedule updateSchedule,List<MultipartFile> files) throws IOException {
        return scheduleServiceConnector.updateSchedule(scheduleId,updateSchedule,files);
    }

    @PostMapping("/{id}")
    public String deleteSchedule(@PathVariable("id")Long scheduleId,
                                 @RequestParam(name = "type", defaultValue = "SINGLE") DeleteType deleteType) {
        scheduleServiceConnector.deleteSchedule(scheduleId,deleteType);
        return "Delete Schedule.";
    }

    @DeleteMapping("/old-schedules")
    public void deleteAllSchedule() {
        scheduleServiceConnector.deleteOldSchedules();
    }
}
