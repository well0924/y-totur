package com.example.inconnector.member;

import com.example.apimodel.member.MemberApiModel;
import com.example.enumerate.member.Roles;
import com.example.enumerate.member.SearchType;
import com.example.interfaces.in_connector.MemberInterfaces;
import com.example.model.member.MemberModel;
import com.example.service.member.MemberService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class MemberInConnector implements MemberInterfaces {

    private final MemberService memberService;

    @Override
    public List<MemberApiModel.Response> findAll() {
        List<MemberModel> memberModelList = memberService.findAll();
        return memberModelList
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<MemberApiModel.Response> findAll(Pageable pageable) {
        Page<MemberModel> memberModelPage = memberService.findAll(pageable);
        return memberModelPage
                .map(this::toResponse);
    }

    @Override
    public Page<MemberApiModel.Response> findAllMemberSearch(String keyword, SearchType searchType, Pageable pageable) {
        Page<MemberModel> memberSearchResult = memberService.findAllMemberSearch(keyword, searchType, pageable);
        return memberSearchResult
                .map(this::toResponse);
    }

    @Override
    public MemberApiModel.Response findById(Long id) {
        MemberModel memberModel = memberService.findById(id);
        return toResponse(memberModel);
    }

    @Override
    public MemberApiModel.Response createMember(MemberApiModel.Request memberModel) {
        MemberModel createMember = MemberModel
                .builder()
                .userId(memberModel.getUserId())
                .password(memberModel.getPassword())
                .userPhone(memberModel.getUserPhone())
                .userEmail(memberModel.getUserEmail())
                .userName(memberModel.getUserName())
                .roles(Roles.ROLE_USER)
                .createdBy(memberModel.getUserId())
                .updatedBy(memberModel.getUserId())
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();

        return toResponse(memberService.createMember(createMember));
    }

    @Override
    public MemberApiModel.Response updateMember(Long id, MemberApiModel.Request memberModel) {
        MemberModel updateModel = MemberModel
                .builder()
                .userId(memberModel.getUserId())
                .userEmail(memberModel.getUserEmail())
                .updatedBy(memberModel.getUserId())
                .userPhone(memberModel.getUserPhone())
                .userName(memberModel.getUserName())
                .build();
        return toResponse(memberService.updateMember(id,updateModel));
    }

    @Override
    public void deleteMember(Long id) {
        memberService.deleteMember(id);
    }

    //model -> response
    public MemberApiModel.Response toResponse(MemberModel memberModel) {
        return MemberApiModel.Response
                .builder()
                .id(memberModel.getId())
                .userId(memberModel.getUserId())
                .password(memberModel.getPassword())
                .userEmail(memberModel.getUserEmail())
                .userPhone(memberModel.getUserPhone())
                .userName(memberModel.getUserName())
                .roles(memberModel.getRoles())
                .createdBy(memberModel.getCreatedBy())
                .updatedBy(memberModel.getUpdatedBy())
                .createdTime(memberModel.getCreatedTime())
                .updatedTime(memberModel.getUpdatedTime())
                .build();
    }
}
