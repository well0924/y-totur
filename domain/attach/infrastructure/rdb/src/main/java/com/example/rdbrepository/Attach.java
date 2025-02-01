package com.example.rdbrepository;

import com.example.jpa.config.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attach extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originFileName;

    private String storedFileName;

    private Long fileSize;

    private String filePath;
    //연관관계
    private Long scheduledId;

    public void update(String originFileName,String storedFileName){
        this.originFileName = originFileName;
        this.storedFileName = storedFileName;
    }
}
