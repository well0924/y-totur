package com.example.model.auth;

import com.example.model.member.MemberModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Builder
@Getter
public class CustomMemberDetails implements UserDetails {

    MemberModel memberModel;

    public CustomMemberDetails(MemberModel memberModel) {
        this.memberModel = memberModel;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(() -> memberModel.getRoles().getValue());
        return authorities;
    }

    @Override
    public String getPassword() {
        return memberModel.getPassword();
    }

    @Override
    public String getUsername() {
        return memberModel.getUserId();
    }
}
