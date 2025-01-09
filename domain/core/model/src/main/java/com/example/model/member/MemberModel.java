package com.example.model.member;

import com.example.enums.Roles;
import com.example.jpa.member.MemberEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class MemberModel {

    private Long id;
    private String userId;
    private String password;
    private String userName;
    private String userPhone;
    private String userEmail;
    private Roles roles;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime updatedTime;

    @Builder
    public MemberModel(MemberEntity memberEntity) {
        this.id  = memberEntity.getId();
        this.userId = memberEntity.getUserId();
        this.password = memberEntity.getPassword();
        this.userName = memberEntity.getUserName();
        this.userPhone = memberEntity.getUserPhone();
        this.userEmail = memberEntity.getUserEmail();
        this.roles = memberEntity.getRoles();
        this.createdTime = memberEntity.getCreatedTime();
        this.updatedTime = memberEntity.getUpdatedTime();
    }

    // 비즈니스 로직
    public boolean isAdmin() {
        return roles == Roles.ROLE_ADMIN;
    }
}
