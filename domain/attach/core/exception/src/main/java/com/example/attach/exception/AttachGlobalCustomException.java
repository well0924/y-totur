package com.example.attach.exception;

import com.example.attach.dto.AttachErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AttachGlobalCustomException {

    @ExceptionHandler(value = AttachCustomExceptionHandler.class)
    protected ResponseEntity<AttachErrorDto> HandleCustomException(AttachCustomExceptionHandler ex) {
        return new ResponseEntity<>(
                new AttachErrorDto(ex.getAttachErrorCode().getStatus(), ex.getAttachErrorCode().getMessage()), HttpStatus.valueOf(ex.getAttachErrorCode().getStatus()));
    }
}
