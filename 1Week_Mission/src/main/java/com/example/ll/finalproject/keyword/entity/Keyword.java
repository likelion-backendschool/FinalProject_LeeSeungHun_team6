package com.example.ll.finalproject.keyword.entity;

import com.example.ll.finalproject.base.entity.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class Keyword extends BaseEntity {
    private String content;
    public String getListUrl() {
        return "/post/list?kwType=hashTag&kw=%s".formatted(content);
    }
}
