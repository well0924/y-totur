package com.example.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    INVALID_PARAMETER(4001, "파라미터 값을 확인해주세요."),
    NOT_FOUND(4002, "존재하않습니다."),
    INTERNAL_SERVER_ERROR(5000, "서버 에러입니다. 서버 팀에 연락주세요!"),
    DB_ERROR(4003,"데이터베이스에 문제가 발생했습니다.");

    private final int status;

    private final String message;
}
