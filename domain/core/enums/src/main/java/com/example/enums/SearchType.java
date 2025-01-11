package com.example.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum SearchType {

    USERID,
    USERNAME,
    USER_EMAIL,
    ALL;

    public static SearchType toSearch(String searchType){
        return switch (searchType){
            case "userID"->USERID;
            case "userName"->USERNAME;
            case "userEmail"->USER_EMAIL;
            default -> ALL;
        };
    }
}
