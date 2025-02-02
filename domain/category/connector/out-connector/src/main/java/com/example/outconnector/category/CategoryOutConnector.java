package com.example.outconnector.category;

import com.example.category.dto.CategoryErrorCode;
import com.example.category.exception.CategoryCustomExceptionHandler;
import com.example.model.category.CategoryModel;
import com.example.rdbrepository.Category;
import com.example.rdbrepository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional
@AllArgsConstructor
public class CategoryOutConnector {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryModel> categoryList() {
        List<CategoryModel> categoryModelList = categoryRepository
                .findAll()
                .stream()
                .map(this::toEntity)
                .collect(Collectors.toList());

        return categoryModelList;
    }

    @Transactional(readOnly = true)
    public void validateCategoryListNotEmpty() {
        if (categoryRepository.count() == 0) {  // 카테고리 테이블에 데이터가 하나도 없으면
            throw new CategoryCustomExceptionHandler(CategoryErrorCode.NOT_FOUND_CATEGORY);  // 예외를 발생시킴
        }
    }

    @Transactional(readOnly = true)
    public CategoryModel findById(Long categoryId) {
        return categoryRepository
                .findById(categoryId)
                .map(this::toEntity)
                .orElseThrow(() -> new CategoryCustomExceptionHandler(CategoryErrorCode.NOT_FOUND_CATEGORY));
    }


    public CategoryModel createCategory(CategoryModel categoryModel) {
        // 카테고리명 중복 여부
        if(categoryRepository.existsByName(categoryModel.getName())) {
            throw new CategoryCustomExceptionHandler(CategoryErrorCode.DUPLICATED_CATEGORY_NAME);
        }

        // 부모 카테고리의 깊이 계산 (기본값이 1임)
        Long depth = (categoryModel.getParentId() == null)
                ? 1L
                : categoryRepository.findById(categoryModel.getParentId())
                .map(Category::getDepth)
                .orElseThrow(() -> new CategoryCustomExceptionHandler(CategoryErrorCode.NOT_FOUND_CATEGORY)) + 1;

        //카테고리 저장
        Category newCategory = Category
                .builder()
                .name(categoryModel.getName())
                .parentId(categoryModel.getParentId())
                .depth(depth)
                .build();

        return toEntity(categoryRepository.save(newCategory));
    }

    public CategoryModel updateCategory(Long categoryId,String name,Long parentId) {
        Category category = categoryRepository
                .findById(categoryId)
                .orElseThrow(()->new CategoryCustomExceptionHandler(CategoryErrorCode.NOT_FOUND_CATEGORY));

        if (!category.getName().equals(name) && categoryRepository.existsByName(name)) {
            throw new CategoryCustomExceptionHandler(CategoryErrorCode.DUPLICATED_CATEGORY_NAME);
        }

        Long depth = (parentId == null)
                ? 1L
                : categoryRepository.findById(parentId)
                .map(Category::getDepth)
                .orElseThrow(() -> new CategoryCustomExceptionHandler(CategoryErrorCode.NOT_FOUND_CATEGORY)) + 1;

        if (categoryId.equals(parentId)) {
            throw new CategoryCustomExceptionHandler(CategoryErrorCode.INVALID_PARENT_CATEGORY);
        }

        category.update(name,parentId,depth);

        return toEntity(categoryRepository.save(category));
    }

    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository
                .findById(categoryId)
                .orElseThrow(()->new CategoryCustomExceptionHandler(CategoryErrorCode.NOT_FOUND_CATEGORY));

        categoryRepository.deleteById(categoryId);
    }

    private CategoryModel toEntity(Category category) {
        return CategoryModel
                .builder()
                .id(category.getId())
                .parentId(category.getParentId())
                .depth(category.getDepth())
                .name(category.getName())
                .createdBy(category.getCreatedBy())
                .createdTime(category.getCreatedTime())
                .updatedBy(category.getUpdatedBy())
                .updatedTime(category.getCreatedTime())
                .build();
    }
}
