package com.example.rdbrepository.member;

import com.example.enumerate.member.Roles;
import com.example.jpa.config.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@EntityListeners(AuditingEntityListener.class) // Auditing 활성화
@NoArgsConstructor
@AllArgsConstructor
public class Member {

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

    @CreatedBy
    private String createdBy; // 생성자

    @LastModifiedBy
    private String updatedBy; // 수정자

    @CreatedDate
    private LocalDateTime createdTime;

    @LastModifiedDate
    private LocalDateTime updatedTime;

    public void update(String userId,String userEmail, String userPhone) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.getUpdatedBy();
    }
}
