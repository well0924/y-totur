package com.example.jpa.member;

import com.example.enums.Roles;
import jakarta.persistence.*;
import jdk.jfr.DataAmount;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private String password;
    private String userName;
    private String userPhone;
    private String userEmail;
    @Enumerated(EnumType.STRING)
    private Roles roles;


    @Builder
    public MemberEntity(Long id, String userId, String password, String userName, String userPhone, String userEmail, Roles roles, LocalDateTime createdTime,LocalDateTime updatedTime) {
        this.id  = id;
        this.userId = userId;
        this.password = password;
        this.userName = userName;
        this.userPhone = userPhone;
        this.userEmail = userEmail;
        this.roles = roles;
        this.createdTime = getCreatedTime();
        this.updatedTime = getUpdatedTime();
    }

    //회원 수정
    public void update(MemberEntity memberEntity) {
        this.userId = memberEntity.getUserId();
        this.userPhone = memberEntity.getUserPhone();
        this.userEmail = memberEntity.getUserEmail();
        this.userName = memberEntity.getUserName();
        this.password = memberEntity.getPassword();
    }
}
