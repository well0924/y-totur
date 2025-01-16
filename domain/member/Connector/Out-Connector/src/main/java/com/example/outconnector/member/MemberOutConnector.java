package com.example.outconnector.member;

import com.example.enumerate.member.Roles;
import com.example.enumerate.member.SearchType;
import com.example.exception.CustomMemberExceptionHandler;
import com.example.exception.MemberErrorCode;
import com.example.model.member.MemberModel;
import com.example.rdbrepository.member.Member;
import com.example.rdbrepository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional
@RequiredArgsConstructor
public class MemberOutConnector {

    private final MemberRepository memberRepository;

    private final MemberRepositoryImpl customMemberRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional(readOnly = true)
    public List<MemberModel> findAll() {
        List<MemberModel> memberModelList = memberRepository
                .findAll()
                .stream()
                .map(this::toEntity)
                .collect(Collectors.toList());

        if(memberModelList.isEmpty()) {
            throw new CustomMemberExceptionHandler(MemberErrorCode.NOT_USER);
        }

        return memberModelList;
    }

    @Transactional(readOnly = true)
    public Page<MemberModel> findAll(Pageable pageable) {
        Page<MemberModel> memberModelPage = memberRepository
                .findAll(pageable)
                .map(this::toEntity);

        if(memberModelPage.isEmpty()) {
            throw new CustomMemberExceptionHandler(MemberErrorCode.NOT_USER);
        }

        return memberModelPage;
    }

    public Page<MemberModel> findAllMemberSearch(String keyword, SearchType searchType, Pageable pageable) {
        return customMemberRepository
                .searchAll(keyword,searchType,pageable)
                .map(this::toEntity);
    }

    @Transactional(readOnly = true)
    public MemberModel findById(Long id) {
        Member memberEntity = memberRepository
                .findById(id)
                .orElseThrow(()-> new CustomMemberExceptionHandler(MemberErrorCode.NOT_USER));

        return toEntity(memberEntity);
    }

    public MemberModel createMember(MemberModel memberModel) {

        Member memberEntity = Member
                .builder()
                .userId(memberModel.getUserId())
                .password(bCryptPasswordEncoder.encode(memberModel.getPassword()))
                .userEmail(memberModel.getUserEmail())
                .userPhone(memberModel.getUserPhone())
                .userName(memberModel.getUserName())
                .createdBy(memberModel.getUserId())
                .createdBy(memberModel.getUserId())
                .updatedBy(memberModel.getUserId())
                .roles(Roles.ROLE_USER)
                .updatedTime(LocalDateTime.now())
                .createdTime(LocalDateTime.now())
                .build();

        return toEntity(memberRepository.save(memberEntity));
    }

    public MemberModel updateMember(Long id, MemberModel memberModel) {
        Member memberEntity = memberRepository.findById(id)
                .orElseThrow(() -> new CustomMemberExceptionHandler(MemberErrorCode.NOT_USER));

        memberEntity.update(memberModel.getUserId(),
                memberModel.getUserEmail(),
                memberModel.getUserPhone(),
                memberModel.getUpdatedBy());

        return toEntity(memberRepository.save(memberEntity));
    }

    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }

    //Entity -> Model
    private MemberModel toEntity(Member memberEntity) {
        return MemberModel.builder()
                .id(memberEntity.getId())
                .userId(memberEntity.getUserId())
                .password(memberEntity.getPassword())
                .userEmail(memberEntity.getUserEmail())
                .userPhone(memberEntity.getUserPhone())
                .userName(memberEntity.getUserName())
                .roles(memberEntity.getRoles())
                .createdBy(memberEntity.getCreatedBy())
                .updatedBy(memberEntity.getUpdatedBy())
                .updatedTime(LocalDateTime.now())
                .createdTime(LocalDateTime.now())
                .build();
    }
}
