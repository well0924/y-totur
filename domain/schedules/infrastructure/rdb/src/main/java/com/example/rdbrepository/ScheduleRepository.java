package com.example.rdbrepository;

import com.example.model.schedules.SchedulesModel;
import com.example.rdbrepository.custom.ScheduleRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedules, Long>, ScheduleRepositoryCustom {
    //삭제가 된 일정들
    @Query(value = "select s from Schedules s where s.isDeletedScheduled = true")
    List<Schedules> findAllByIsDeletedScheduled();

    //일정 일괄 삭제
    @Modifying
    @Query("DELETE FROM Schedules s WHERE s.isDeletedScheduled = true AND s.endTime < :thresholdDate")
    int deleteOldSchedules(@Param("thresholdDate") LocalDateTime thresholdDate);

}
