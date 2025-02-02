package com.example.rdb.member;

import com.example.rdb.member.custom.MemberRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long>, MemberRepositoryCustom {

    //시큐리티 인증
    Optional<Member> findByUserId(String userId);
}
