package com.example.exception.exception;

import com.example.exception.dto.MemberErrorCode;
import lombok.Getter;

@Getter
public class MemberCustomExceptionHandler extends RuntimeException {

    private final MemberErrorCode memberErrorCode;

    public MemberCustomExceptionHandler(String message, MemberErrorCode errorCode) {
        super(message);
        this.memberErrorCode = errorCode;
    }

    public MemberCustomExceptionHandler(MemberErrorCode errorCode) {
        super(errorCode.getMessage());
        this.memberErrorCode = errorCode;
    }

    public MemberErrorCode getMemberErrorCode() {
        return memberErrorCode;
    }
}
