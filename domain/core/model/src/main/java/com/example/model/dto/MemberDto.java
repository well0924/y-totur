package com.example.model.dto;

import com.example.enums.Roles;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDateTime;

public class MemberDto {

    @Setter
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {

        @NotBlank(message = "아이디는 필수 입력값입니다.")
        @Pattern(regexp = "^[a-z0-9]{4,20}$", message = "아이디는 영어 소문자와 숫자만 사용하여 4~20자리여야 합니다.")
        private String userId;

        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,50}$",
                message = "비밀번호는 8~50자리수여야 합니다. 영문 대소문자, 숫자, 특수문자를 1개 이상 포함해야 합니다.")
        private String password;

        @NotBlank(message="회원이름을 입력해주세요.")
        @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,10}$" , message = "회원이름은 2~10자리여야 합니다.")
        private String userName;

        @NotBlank(message = "회원 전화번호를 입력해주세요.")
        private String userPhone;

        @Email
        private String userEmail;

    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String userId;
        private String password;
        private String userName;
        private String userEmail;
        private Roles roles;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        private LocalDateTime createdTime;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        private LocalDateTime updatedTime;
    }
}
