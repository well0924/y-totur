package com.example.enumerate.schedules;

import com.example.exception.schedules.dto.ScheduleErrorCode;
import com.example.exception.schedules.exception.ScheduleCustomException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PROGRESS_STATUS {

    IN_COMPLETE("IN_COMPLETE"),
    PROGRESS("PROGRESS"),
    COMPLETE("COMPLETE");

    private final String value;

    @JsonCreator
    public static PROGRESS_STATUS fromString(String value) {
        for (PROGRESS_STATUS status : PROGRESS_STATUS.values()) {
            if (status.getValue().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new ScheduleCustomException(ScheduleErrorCode.INVALID_PROGRESS_STATUS);
    }
}
