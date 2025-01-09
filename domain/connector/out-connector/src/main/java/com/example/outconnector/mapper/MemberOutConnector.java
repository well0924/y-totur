package com.example.outconnector.mapper;

import com.example.common.dto.ErrorCode;
import com.example.common.exception.CustomExceptionHandler;
import com.example.enums.Roles;
import com.example.enums.SearchType;
import com.example.interfaces.member.MemberConnectorInterface;
import com.example.jpa.member.MemberEntity;
import com.example.model.dto.MemberDto;
import com.example.model.member.MemberModel;
import com.example.outconnector.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class MemberOutConnector implements MemberConnectorInterface {

    private final MemberRepository memberRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public Page<MemberModel> findByAllSearch(String keyword, SearchType searchType, Pageable pageable) {
        return memberRepository.findByAllSearch(keyword, searchType, pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public List<MemberModel> findAll() {
        List<MemberModel> list = memberRepository.findAll()
                .stream()
                .map(this::toModel)
                .collect(Collectors.toList());
        log.info("list::"+ list);
        return list;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<MemberModel> findAll(Pageable pageable) {
        Page<MemberEntity> memberEntities = memberRepository.findAll(pageable);
        return memberEntities.map(this::toModel);
    }

    @Transactional(readOnly = true)
    @Override
    public MemberModel findById(Long id) {
        MemberEntity memberEntity = memberRepository.findById(id)
                .orElseThrow(()->new CustomExceptionHandler(ErrorCode.NOT_FOUND));
        return toModel(memberEntity);
    }

    @Transactional
    @Override
    public MemberModel save(MemberDto.Request model) {
        MemberEntity memberEntity = toEntity(model);
        MemberEntity saveEntity = memberRepository.save(memberEntity);
        return toModel(saveEntity);
    }

    @Transactional
    @Override
    public MemberModel update(Long id, MemberDto.Request member) {
        MemberEntity memberEntity = memberRepository.findById(id)
                .orElseThrow(()->new CustomExceptionHandler(ErrorCode.NOT_FOUND));
        memberEntity.update(memberEntity);
        memberRepository.save(memberEntity);
        return toModel(memberEntity);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        MemberEntity memberEntity = memberRepository.findById(id)
                .orElseThrow(()->new CustomExceptionHandler(ErrorCode.NOT_FOUND));
        memberRepository.delete(memberEntity);
    }

    @Transactional
    @Override
    public boolean existsByUserId(String userId) {
        return memberRepository.existsByUserId(userId);
    }

    @Transactional
    @Override
    public boolean existsByUserEmail(String userEmail) {
        return memberRepository.existsByUserEmail(userEmail);
    }

    private MemberEntity toEntity(MemberDto.Request memberModel) {
        return MemberEntity
                .builder()
                .userId(memberModel.getUserId())
                .password(passwordEncoder.encode(memberModel.getPassword()))
                .userName(memberModel.getUserName())
                .userPhone(memberModel.getUserPhone())
                .userEmail(memberModel.getUserEmail())
                .roles(Roles.ROLE_USER)
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();
    }

    private MemberModel toModel(MemberEntity memberEntity) {
        return MemberModel
                .builder()
                .memberEntity(memberEntity)
                .build();
    }
}
