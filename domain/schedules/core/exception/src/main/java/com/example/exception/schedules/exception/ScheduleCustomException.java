package com.example.exception.schedules.exception;

import com.example.exception.schedules.dto.ScheduleErrorCode;
import lombok.Getter;

@Getter
public class ScheduleCustomException extends RuntimeException {

    private final ScheduleErrorCode scheduleErrorCode;

    public ScheduleCustomException(ScheduleErrorCode scheduleErrorCode,Object... args) {
        super(scheduleErrorCode.formatMessage(args));
        this.scheduleErrorCode = scheduleErrorCode;
    }

    public ScheduleCustomException(ScheduleErrorCode scheduleErrorCode) {
        super(scheduleErrorCode.getMessage());
        this.scheduleErrorCode = scheduleErrorCode;
    }

    public ScheduleErrorCode getScheduleErrorCode(){
        return scheduleErrorCode;
    }
}
