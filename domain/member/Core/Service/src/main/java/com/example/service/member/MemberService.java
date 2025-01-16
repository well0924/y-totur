package com.example.service.member;

import com.example.enumerate.member.SearchType;
import com.example.model.member.MemberModel;
import com.example.outconnector.member.MemberOutConnector;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MemberService {

    private final MemberOutConnector memberOutConnector;

    public List<MemberModel> findAll()  {
        return memberOutConnector.findAll();
    }

    public Page<MemberModel> findAll(Pageable pageable) {
        return memberOutConnector.findAll(pageable);
    }

    public Page<MemberModel> findAllMemberSearch(String keyword, SearchType searchType, Pageable pageable) {
        return memberOutConnector.findAllMemberSearch(keyword, searchType, pageable);
    }

    public MemberModel findById(Long id) {
        return memberOutConnector.findById(id);
    }

    public MemberModel createMember(MemberModel memberModel) {
        memberModel.isValidEmail();
        memberModel.isValidPhoneNumber();
        memberModel.isValidUserId();
        return memberOutConnector.createMember(memberModel);
    }

    public MemberModel updateMember(Long id, MemberModel memberModel) {
        return memberOutConnector.updateMember(id,memberModel);
    }

    public void deleteMember(Long id) {
        memberOutConnector.deleteMember(id);
    }
}