package com.example.apimodel.member;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

public class MemberApiModel {

    @Setter
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request  {
        private String userId;
        private String password;
        private String userEmail;
        private String userName;
        private String userPhone;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String userId;
        private String password;
        private String userEmail;
        private String userPhone;
        private String userName;
        private String roles;
        private String createdBy;
        private String updatedBy;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        private LocalDateTime createdTime;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        private LocalDateTime updatedTime;
    }
}
