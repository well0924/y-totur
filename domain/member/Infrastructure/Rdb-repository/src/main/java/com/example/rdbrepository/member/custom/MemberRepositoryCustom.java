package com.example.rdbrepository.member.custom;

import com.example.enumerate.member.SearchType;
import com.example.rdbrepository.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberRepositoryCustom {
    //회원 검색
    Page<Member> searchAll(String keyword, SearchType searchType, Pageable pageable);
}
