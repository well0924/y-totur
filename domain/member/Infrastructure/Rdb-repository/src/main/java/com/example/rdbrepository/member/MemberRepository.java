package com.example.rdbrepository.member;

import com.example.rdbrepository.member.custom.MemberRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository< Member,Long>, MemberRepositoryCustom {

    //시큐리티 인증
    Optional<Member> findByUserId(String userId);
}
