package com.example.rdbrepository;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

@Getter
@Entity
public class Schedules {
    @Id
    private Long id;

    public void setId(Long id) {
        this.id = id;
    }

}
