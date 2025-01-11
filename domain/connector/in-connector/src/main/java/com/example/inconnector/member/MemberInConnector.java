package com.example.inconnector.member;

import com.example.enums.SearchType;
import com.example.interfaces.member.MemberConnectorInterface;
import com.example.model.dto.MemberDto;
import com.example.model.member.MemberModel;
import com.example.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MemberInConnector implements MemberConnectorInterface {

    private final MemberService memberService;

    @Override
    public List<MemberModel> findAll() {
        return memberService.findAll();
    }

    @Override
    public Page<MemberModel> findAll(Pageable pageable) {
        return memberService.findAll(pageable);
    }

    @Override
    public MemberModel findById(Long id) {
        return memberService.findById(id);
    }

    @Override
    public MemberModel save(MemberDto.Request model) {
        return memberService.save(model);
    }

    @Override
    public MemberModel update(Long id, MemberDto.Request member) {
        return memberService.update(id, member);
    }

    @Override
    public void deleteById(Long id) {
        memberService.deleteById(id);
    }

    @Override
    public boolean existsByUserId(String userId) {
        return memberService.existsByUserId(userId);
    }

    @Override
    public boolean existsByUserEmail(String userEmail) {
        return memberService.existsByUserEmail(userEmail);
    }

    @Override
    public Page<MemberModel> findByAllSearch(String keyword, SearchType searchType, Pageable pageable) {
        return memberService.findByAllSearch(keyword,searchType, pageable);
    }
}
