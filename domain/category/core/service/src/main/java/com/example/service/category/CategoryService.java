package com.example.service.category;

import com.example.outconnector.category.CategoryOutConnector;
import com.example.category.dto.CategoryErrorCode;
import com.example.category.exception.CategoryCustomExceptionHandler;
import com.example.model.category.CategoryModel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class CategoryService {

    private final CategoryOutConnector categoryOutConnector;

    @Transactional(readOnly = true)
    public List<CategoryModel> getAllCategories() {
        categoryOutConnector.validateCategoryListNotEmpty();
        return categoryOutConnector.categoryList();
    }

    @Transactional(readOnly = true)
    public CategoryModel getCategoryById(Long categoryId) {
        return categoryOutConnector.findById(categoryId);
    }

    public CategoryModel createCategory(CategoryModel categoryModel) {
        // 기존 카테고리 목록 조회
        List<CategoryModel> existingCategories = categoryOutConnector.categoryList();

        // 카테고리 목록이 비어있지 않을 때만 중복 확인
        if (!existingCategories.isEmpty()) {
            if (categoryModel.isDuplicateName(existingCategories)) {
                throw new CategoryCustomExceptionHandler(CategoryErrorCode.DUPLICATED_CATEGORY_NAME);
            }
        }

        // 부모-자식 관계 설정
        categoryModel.setChildCategory(categoryModel.getParentId(), categoryModel.getCreatedBy());

        // 카테고리 생성
        return categoryOutConnector.createCategory(categoryModel);
    }

    public CategoryModel updateCategory(Long categoryId,CategoryModel categoryModel) {
        // 기존 카테고리 조회
        CategoryModel existingCategory = categoryOutConnector.findById(categoryId);

        // 카테고리 이름 중복 확인
        if (!existingCategory.getName().equals(categoryModel.getName())) {
            List<CategoryModel> existingCategories = categoryOutConnector.categoryList();
            if (categoryModel.isDuplicateName(existingCategories)) {
                throw new CategoryCustomExceptionHandler(CategoryErrorCode.DUPLICATED_CATEGORY_NAME);
            }
        }

        // 부모-자식 관계 설정
        existingCategory.setChildCategory(categoryModel.getParentId(), categoryModel.getUpdatedBy());

        // 카테고리 업데이트
        return categoryOutConnector.updateCategory(
                existingCategory.getId(),
                categoryModel.getName(),
                categoryModel.getParentId()
        );
    }

    public void deleteCategory(Long categoryId) {
        categoryOutConnector.deleteCategory(categoryId);
    }
}
