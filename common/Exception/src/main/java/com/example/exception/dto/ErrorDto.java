package com.example.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ErrorDto {

    private Integer errorCode;

    private String message;
}
