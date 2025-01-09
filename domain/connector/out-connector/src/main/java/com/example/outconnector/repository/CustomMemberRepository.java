package com.example.outconnector.repository;

import com.example.enums.SearchType;
import com.example.model.member.MemberModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomMemberRepository {
    //회원 검색
    Page<MemberModel> findByAllSearch(String keyword, SearchType searchType, Pageable pageable);
}
