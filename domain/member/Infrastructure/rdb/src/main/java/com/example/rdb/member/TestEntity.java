package com.example.rdb.member;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class TestEntity {
    @Id
    private Long id;

    public void setId(Long id) {

        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
