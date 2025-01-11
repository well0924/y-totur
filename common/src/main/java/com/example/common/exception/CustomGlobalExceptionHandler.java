package com.example.common.exception;

import com.example.common.dto.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomGlobalExceptionHandler {

    @ExceptionHandler(value= Exception.class)
    public ResponseEntity<?>IllegalArgumentException(Exception e){
        return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    @ExceptionHandler({CustomExceptionHandler.class})
    protected ResponseEntity<ErrorDto> HandleCustomException(CustomExceptionHandler ex) {
        return new ResponseEntity<>(
                new ErrorDto(ex.getErrorCode().getStatus(), ex.getErrorCode().getMessage()), HttpStatus.valueOf(ex.getErrorCode().getStatus()));
    }

}
