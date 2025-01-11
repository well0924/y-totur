package com.example.outconnector.repository;

import com.example.jpa.member.MemberEntity;
import com.example.model.member.MemberModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity,Long> ,CustomMemberRepository{

    //회원 아이디 중복
    boolean existsByUserId(String userId);
    //회원 이메일 중복
    boolean existsByUserEmail(String userEmail);
    //시큐리티 로그인(회원조회)
    Optional<MemberModel> findByUserId(String userId);
}
