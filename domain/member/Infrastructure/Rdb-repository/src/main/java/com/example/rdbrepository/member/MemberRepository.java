package com.example.rdbrepository.member;

import com.example.rdbrepository.member.custom.MemberRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository< Member,Long>, MemberRepositoryCustom {

}
