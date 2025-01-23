package com.example.schedule;

import com.example.rdbrepository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduleOutConnector {

    private final ScheduleRepository scheduleRepository;
}
