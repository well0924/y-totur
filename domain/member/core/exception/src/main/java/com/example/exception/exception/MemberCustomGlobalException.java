package com.example.exception.exception;

import com.example.exception.dto.MemberErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class MemberCustomGlobalException {

    @ExceptionHandler(value = MemberCustomExceptionHandler.class)
    protected ResponseEntity<MemberErrorDto> HandleCustomException(MemberCustomExceptionHandler ex) {
        return new ResponseEntity<>(
                MemberErrorDto
                        .builder()
                        .errorCode(ex.getMemberErrorCode().getStatus())
                        .message(ex.getMessage())
                        .build(),
                HttpStatus.valueOf(ex.getMemberErrorCode().getStatus()));
    }

}
