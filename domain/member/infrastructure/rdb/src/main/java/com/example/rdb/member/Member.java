package com.example.rdb.member;

import com.example.enumerate.member.Roles;
import com.example.jpa.config.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String userId;
    @Column
    private String password;
    @Column
    private String userPhone;
    @Column
    private String userEmail;
    @Column
    private String userName;
    @Enumerated(EnumType.STRING)
    private Roles roles;

    public void update(String userId,String userEmail, String userPhone) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.setUpdatedBy(userId);
    }
}
