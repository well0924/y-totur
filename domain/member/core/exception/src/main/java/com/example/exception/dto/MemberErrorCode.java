package com.example.exception.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode {

    //user error
    USERID_DUPLICATE(40001, "회원 아이디 '%s'가 중복됩니다."),
    USER_EMAIL_DUPLICATE(40001, "회원 이메일 '%s'가 중복됩니다."),
    NOT_FIND_USERID(50001, "회원 아이디 '%s'를 찾을 수 없습니다."),
    NOT_SEARCH_USER(4006, "검색된 회원이 없습니다."),
    NOT_USER(4007, "회원이 존재하지 않습니다."),
    NOT_PASSWORD_MATCH(4008, "비밀번호가 일치하지 않습니다.");

    private final int status;

    private final String message;

    // 동적 메시지 생성을 위한 메서드
    public String formatMessage(Object... args) {
        return String.format(this.message, args);
    }
}
