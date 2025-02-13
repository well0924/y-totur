package com.example.rdbrepository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,Long> {

    //카테고리 이름 중복
    boolean existsByName(String categoryName);
    //카테고리 부모 번호 확인
    boolean existsByParentId(Long parentId);
}
