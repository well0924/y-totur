package com.example.attach.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AttachErrorCode {

    NOT_FOUND_ATTACH_LIST(40012,"첨부파일의 목록이 없습니다."),
    NOT_FOUND_ATTACH(40013,"첨부파일을 찾을 수 없습니다.");

    private final int status;

    private final String message;
}
