package com.example.exception.exception;

import com.example.exception.dto.MemberErrorCode;
import lombok.Getter;

@Getter
public class MemberCustomExceptionHandler extends RuntimeException {

    private final MemberErrorCode memberErrorCode;

    public MemberCustomExceptionHandler(MemberErrorCode errorCode, Object... args) {
        super(errorCode.formatMessage(args));
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
