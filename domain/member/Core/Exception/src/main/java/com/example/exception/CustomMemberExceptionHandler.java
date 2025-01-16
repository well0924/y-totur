package com.example.exception;


public class CustomMemberExceptionHandler extends RuntimeException {

    private final MemberErrorCode memberErrorCode;

    public CustomMemberExceptionHandler(String message, MemberErrorCode errorCode) {
        super(message);
        this.memberErrorCode = errorCode;
    }

    public CustomMemberExceptionHandler(MemberErrorCode errorCode) {
        super(errorCode.getMessage());
        this.memberErrorCode = errorCode;
    }

    public MemberErrorCode getMemberErrorCode() {
        return memberErrorCode;
    }
}
