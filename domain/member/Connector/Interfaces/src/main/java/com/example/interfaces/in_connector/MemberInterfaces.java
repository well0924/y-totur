package com.example.interfaces.in_connector;

import com.example.apimodel.member.MemberApiModel;
import com.example.enumerate.member.SearchType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberInterfaces {

    List<MemberApiModel.Response> findAll();
    Page<MemberApiModel.Response> findAll(Pageable pageable);
    Page<MemberApiModel.Response> findAllMemberSearch(String keyword, SearchType searchType, Pageable pageable);
    MemberApiModel.Response findById(Long id);
    MemberApiModel.Response createMember(MemberApiModel.Request memberModel);
    MemberApiModel.Response updateMember(Long id, MemberApiModel.Request memberModel);
    void deleteMember(Long id);
}
