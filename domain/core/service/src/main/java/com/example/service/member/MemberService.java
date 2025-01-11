package com.example.service.member;

import com.example.common.dto.ErrorCode;
import com.example.common.exception.CustomExceptionHandler;
import com.example.enums.SearchType;
import com.example.interfaces.member.MemberConnectorInterface;
import com.example.model.dto.MemberDto;
import com.example.model.member.MemberModel;
import com.example.outconnector.mapper.MemberOutConnector;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
@AllArgsConstructor
public class MemberService implements MemberConnectorInterface {

    private final MemberOutConnector mapper;

    @Override
    public List<MemberModel> findAll() {
        List<MemberModel> list = mapper.findAll();
        if(list.isEmpty()) {
            throw new CustomExceptionHandler(ErrorCode.NOT_FOUND);
        }
        log.info("list::"+list);
        return list;
    }

    @Override
    public Page<MemberModel> findAll(Pageable pageable) {
        Page<MemberModel>memberEntities = mapper.findAll(pageable);
        if(memberEntities.isEmpty()){
            throw new CustomExceptionHandler(ErrorCode.NOT_USER);
        }
        return memberEntities;
    }

    @Override
    public Page<MemberModel> findByAllSearch(String keyword, SearchType searchType, Pageable pageable) {
        Page<MemberModel>searchResult = mapper.findByAllSearch(keyword, searchType ,pageable);
        if(searchResult.isEmpty()){
            throw new CustomExceptionHandler(ErrorCode.NOT_FOUND);
        }
        return searchResult;
    }

    @Override
    public MemberModel findById(Long id) {
        return mapper.findById(id);
    }

    @Override
    public MemberModel save(MemberDto.Request model) {
        return mapper.save(model);
    }

    @Override
    public MemberModel update(Long id, MemberDto.Request member) {
        return mapper.update(id, member);
    }

    @Override
    public void deleteById(Long id) {
        mapper.deleteById(id);
    }

    @Override
    public boolean existsByUserId(String userId) {
        return mapper.existsByUserId(userId);
    }

    @Override
    public boolean existsByUserEmail(String userEmail) {
        return mapper.existsByUserEmail(userEmail);
    }
}
