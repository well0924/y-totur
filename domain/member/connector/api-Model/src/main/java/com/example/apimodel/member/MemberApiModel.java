package com.example.apimodel.member;

import com.example.enumerate.member.Roles;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

public class MemberApiModel {

    public record CreateRequest(
            @NotBlank(message = "회원의 아이디는 필수값입니다.")
            @Pattern(
                    regexp = "^(?=.*[a-z]).{8,}$",
                    message = "아이디는 최소 8자 이상이며, 반드시 하나 이상의 소문자를 포함해야 합니다."
            )
            String userId,
            @NotBlank(message = "비밀번호는 필수값입니다.")
            @Pattern(
                    regexp = "^(?=.*[0-9])(?=.*[!@#$%^&*(),.?\":{}|<>])(?=.*[a-zA-Z]).{8,}$",
                    message = "비밀번호는 숫자,특수문자를 포함해서 8자이상 입니다."
            )
            String password,
            @NotBlank(message = "이메일은 필수값입니다.")
            @Email(message = "이메일 형식이 아닙니다.")
            String userEmail,
            @NotBlank(message = "회원 이름은 필수값입니다.")
            @Size(min = 2, message = "회원 이름은 최소 2글자 이상이어야 합니다.")
            String userName,
            @NotBlank(message = "회원의 전화번호는 필수값입니다.")
            @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$",
                    message = "전화번호는 00-000-0000 또는 000-0000-0000 형식이어야 합니다.")
            String userPhone
    ) {}

    public record UpdateRequest(
            String userId,
            String userEmail,
            String userName,
            String userPhone
    ) {}

    @Builder
    public record MemberResponse(
            Long id,
            String userId,
            String password,
            String userEmail,
            String userPhone,
            String userName,
            Roles roles,
            String createdBy,
            String updatedBy,
            LocalDateTime createdTime,
            LocalDateTime updatedTime
    ) {

    }
}
