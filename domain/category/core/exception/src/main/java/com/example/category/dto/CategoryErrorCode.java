package com.example.category.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CategoryErrorCode {

    //category error
    NOT_FOUND_CATEGORY(40009, "카테고리 '%s'를 찾을 수 없습니다."),
    DUPLICATED_CATEGORY_NAME(40010, "카테고리 이름 '%s'이(가) 중복됩니다."),
    INVALID_PARENT_CATEGORY(40011, "유효하지 않은 부모 카테고리입니다."),
    CANNOT_DELETE_CATEGORY_WITH_CHILDREN(40012, "자식 카테고리와 함께 삭제할 수 없습니다.");

    private final int status;

    private final String message;

    public String formatMessage(Object... args) {
        return String.format(this.message, args);
    }
}
