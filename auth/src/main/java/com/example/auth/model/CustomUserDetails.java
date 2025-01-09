package com.example.auth.model;

import com.example.model.member.MemberModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

@Getter
@AllArgsConstructor
public class CustomUserDetails implements UserDetails, Serializable {

    private final MemberModel model;

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
        Collection<GrantedAuthority> collectors = new ArrayList<>();
        //model부분에 있는 enums 타입이 접근이 안됨....
        collectors.add(()->"ROLE_USER");
        //collectors.add(new SimpleGrantedAuthority(model.getRoles().getValue()));
        return collectors;
    }

    @Override
    public String getPassword() {
        return model.getPassword();
    }

    @Override
    public String getUsername() { return model.getUserId(); }

}
