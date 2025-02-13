package com.example.rdbrepository;

import com.example.enumerate.schedules.PROGRESS_STATUS;
import com.example.jpa.config.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Schedules extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contents;

    private int scheduleMonth;

    private int scheduleDay;
    //회원의 번호
    private Long userId;
    //카테고리의 번호
    private Long categoryId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private PROGRESS_STATUS PROGRESS_STATUS;

}
