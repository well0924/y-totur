package com.example.auth.service;

import com.example.auth.model.CustomUserDetails;
import com.example.common.dto.ErrorCode;
import com.example.common.exception.CustomExceptionHandler;
import com.example.jpa.member.MemberEntity;
import com.example.model.member.MemberModel;
import com.example.outconnector.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<MemberModel>memberEntity = Optional.ofNullable(memberRepository.findByUserId(username)
                .orElseThrow(() -> new CustomExceptionHandler(ErrorCode.NOT_USER)));
        if(memberEntity.isPresent()){
            return new CustomUserDetails(memberEntity.get());
        }
        return null;
    }
}
