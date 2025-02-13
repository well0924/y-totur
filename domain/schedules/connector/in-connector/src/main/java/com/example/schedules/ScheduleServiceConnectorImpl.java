package com.example.schedules;

import com.example.schedule.ScheduleDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduleServiceConnectorImpl {

    private final ScheduleDomainService scheduleDomainService;

}
