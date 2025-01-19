package com.example.enumerate.schedules;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProgressStatus {

    COMPLETE("complete"),
    PROGRESS("progress"),
    INCOMPLETE("incomplete");

    private final String value;
}
