package com.example.rdbrepository.schedules;

import com.example.enumerate.schedules.ProgressStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String contents;
    private int startTime;
    private int endTime;
    @Enumerated(value = EnumType.STRING)
    private ProgressStatus progressStatus;
    private Long userId;
    private Long categoryId;
    private Long parentScheduleId;
    private int scheduleMonth;
    private int scheduleDay;
}
