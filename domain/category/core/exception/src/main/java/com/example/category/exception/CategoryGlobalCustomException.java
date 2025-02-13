package com.example.category.exception;

import com.example.category.dto.CategoryErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CategoryGlobalCustomException {

    @ExceptionHandler(value = CategoryCustomExceptionHandler.class)
    protected ResponseEntity<CategoryErrorDto> HandleCustomException(CategoryCustomExceptionHandler ex) {
        return new ResponseEntity<>(
                CategoryErrorDto
                        .builder()
                        .errorCode(ex.getCategoryErrorCode().getStatus())
                        .message(ex.getMessage())
                        .build(),
                HttpStatus.valueOf(ex.getCategoryErrorCode().getStatus()));
    }
}
