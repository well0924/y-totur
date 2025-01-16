package com.example.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode {

    //user error
    USERID_DUPLICATE(400,"회원 아이디가 중복이 됩니다."),
    USER_EMAIL_DUPLICATE(400,"회원 이메일이 중복이 됩니다."),
    NOT_FIND_USERID(404,"회원아이디를 찾을 수가 없습니다."),
    NOT_SEARCH_USER(404,"검색된 회원이 없습니다."),
    NOT_USER(404,"회원이 존재하지 않습니다."),
    NOT_PASSWORD_MATCH(400,"비밀번호가 일치하지 않습니다.");

    private final int status;

    private final String message;
}
