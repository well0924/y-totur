package com.example.rdbrepository.member;

import com.example.enumerate.member.Roles;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
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
    private String createdBy;
    @LastModifiedBy
    private String updatedBy;
    @CreatedDate
    private LocalDateTime createdTime;
    @UpdateTimestamp
    private LocalDateTime updatedTime;

    public void update(String userId,String userEmail, String userPhone, String updatedBy) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.updatedBy = updatedBy;
    }
}
