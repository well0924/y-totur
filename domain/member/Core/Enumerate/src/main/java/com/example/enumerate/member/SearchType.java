package com.example.enumerate.member;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SearchType {
    USERID("userId"),
    MEMBER_NAME("memberName"),
    EMAIL("memberEmail"),
    ALL("All");

    private final String value;

    @JsonCreator
    public static SearchType toSearch(String value) {
        for (SearchType type : SearchType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid SearchType: " + value);
    }
}
