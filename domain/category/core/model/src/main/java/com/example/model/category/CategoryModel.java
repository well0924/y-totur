package com.example.model.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryModel {

    private Long id;
    private String name;
    private Long parentId;
    private Long depth;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    public void setChildCategory(Long parentId, String createdBy) {
        if (parentId != null && parentId == this.id) {
            throw new IllegalArgumentException("A category cannot be its own parent.");
        }

        this.parentId = parentId;
        this.depth = (parentId == null) ? 1L : getDepth() + 1;  // 부모 카테고리에서 1 깊이 추가
        this.createdBy = createdBy;
        this.createdTime = LocalDateTime.now();
    }

    public Long getDepth() {
        return (depth == null) ? 0L : depth;
    }

    // 계층이 루트인지 확인
    public boolean isRootCategory() {
        return parentId == null || parentId == 0L;
    }

    // 카테고리 이름 중복 검사를 추가
    public boolean isDuplicateName(List<CategoryModel> categories) {
        for (CategoryModel category : categories) {
            if (this.name.equals(category.getName())) {
                return true;
            }
        }
        return false;
    }

    // 상위 카테고리로 되돌리기 (setParentCategory) -> 수정을 할때 사용.
    public void setParentCategory(Long parentId) {
        this.parentId = parentId;
        this.depth = (parentId == null) ? 1L : getDepth() + 1;
    }

}
