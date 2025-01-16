package com.example.exception;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(2)
@RestControllerAdvice
public class CustomMemberGlobalExceptionHandler {

    @ExceptionHandler({CustomMemberExceptionHandler.class})
    protected ResponseEntity<ErrorDto> HandleCustomException(CustomMemberExceptionHandler ex) {
        return new ResponseEntity<>(
                new ErrorDto(ex.getMemberErrorCode().getStatus(), ex.getMemberErrorCode().getMessage()), HttpStatus.valueOf(ex.getMemberErrorCode().getStatus()));
    }
}
