package com.example.enumerate.schedules;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PROGRESS_STATUS {

    IN_COMPLETE("in_complete"),
    PROGRESS("progress"),
    COMPLETE("complete");

    private final String name;
}
