package com.example.category.exception;

import com.example.category.dto.CategoryErrorCode;
import lombok.Getter;

@Getter
public class CategoryCustomException extends RuntimeException {

    private final CategoryErrorCode categoryErrorCode;

    public CategoryCustomException(CategoryErrorCode categoryErrorCode, Object ...args) {
        super(categoryErrorCode.formatMessage(args));
        this.categoryErrorCode = categoryErrorCode;
    }

    public CategoryCustomException(CategoryErrorCode categoryErrorCode) {
        super(categoryErrorCode.getMessage());
        this.categoryErrorCode = categoryErrorCode;
    }

    public CategoryErrorCode getCategoryErrorCode() { return categoryErrorCode; }
}
