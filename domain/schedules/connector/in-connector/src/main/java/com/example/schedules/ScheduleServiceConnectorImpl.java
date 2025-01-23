package com.example.schedules;

import com.example.schedule.ScheduleDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduleServiceConnectorImpl implements ScheduleServiceConnector {

    private final ScheduleDomainService scheduleDomainService;

    @Override
    public long createSchedule() {
        return 0;
    }
}
