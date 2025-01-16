package com.example.enumerate.member;

public enum SearchType {
    USERID,
    MEMBER_NAME,
    EMAIL,
    ALL;
    public static SearchType toSearch(String searchType){
        return switch (searchType){
            case "userId"->USERID;
            case "memberName"->MEMBER_NAME;
            case "memberEmail"->EMAIL;
            default -> ALL;
        };
    }
}
