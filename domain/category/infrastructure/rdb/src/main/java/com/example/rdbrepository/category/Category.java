package com.example.rdbrepository.category;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

@Getter
@Entity
public class Category {
    @Id
    private Long id;

    public void setId(Long id) {

        this.id = id;
    }

}
