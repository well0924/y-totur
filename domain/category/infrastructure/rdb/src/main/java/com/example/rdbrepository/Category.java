package com.example.rdbrepository;

import com.example.jpa.config.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Long parentId;
    @Column(nullable = false)
    @ColumnDefault("0")
    private Long depth = 0L;

    public void setId(Long id) {
        this.id = id;
    }

    public void update(String name,Long parentId,Long depth) {
        this.name = name;
        this.parentId = parentId;
        this.depth = depth;
    }
}
