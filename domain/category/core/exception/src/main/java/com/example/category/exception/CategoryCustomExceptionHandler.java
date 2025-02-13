package com.example.category.exception;

import com.example.category.dto.CategoryErrorCode;
import lombok.Getter;

@Getter
public class CategoryCustomExceptionHandler extends RuntimeException {

    private final CategoryErrorCode categoryErrorCode;

    public CategoryCustomExceptionHandler(CategoryErrorCode categoryErrorCode,Object ...args) {
        super(categoryErrorCode.formatMessage(args));
        this.categoryErrorCode = categoryErrorCode;
    }

    public CategoryCustomExceptionHandler(CategoryErrorCode categoryErrorCode) {
        super(categoryErrorCode.getMessage());
        this.categoryErrorCode = categoryErrorCode;
    }

    public CategoryErrorCode getCategoryErrorCode() { return categoryErrorCode; }
}
