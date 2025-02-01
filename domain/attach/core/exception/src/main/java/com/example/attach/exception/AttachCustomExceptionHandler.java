package com.example.attach.exception;

import com.example.attach.dto.AttachErrorCode;
import lombok.Getter;

@Getter
public class AttachCustomExceptionHandler extends RuntimeException {

    private final AttachErrorCode attachErrorCode;

    public AttachCustomExceptionHandler(String message, AttachErrorCode attachErrorCode) {
        super(message);
        this.attachErrorCode = attachErrorCode;
    }

    public AttachCustomExceptionHandler(AttachErrorCode attachErrorCode) {
        super(attachErrorCode.getMessage());
        this.attachErrorCode = attachErrorCode;
    }
}
