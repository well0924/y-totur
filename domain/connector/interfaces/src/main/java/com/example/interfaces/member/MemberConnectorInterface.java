package com.example.interfaces.member;

import com.example.enums.SearchType;
import com.example.model.dto.MemberDto;
import com.example.model.member.MemberModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberConnectorInterface {
    //목록
    List<MemberModel> findAll();
    //목록(페이징)
    Page<MemberModel> findAll(Pageable pageable);
    //회원 검색
    Page<MemberModel> findByAllSearch(String keyword, SearchType searchType, Pageable pageable);
    //단일조회
    MemberModel findById(Long id);
    //저장
    MemberModel save(MemberDto.Request model);
    //수정
    MemberModel update(Long id,MemberDto.Request member);
    //삭제
    void deleteById(Long id);
    //회원 아이디 중복
    boolean existsByUserId(String userId);
    //회원 이메일 중복
    boolean existsByUserEmail(String userEmail);

}
