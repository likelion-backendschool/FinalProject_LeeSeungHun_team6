package com.example.ll.finalproject.app.hashTag.entity;

import com.example.ll.finalproject.app.article.entity.Article;
import com.example.ll.finalproject.app.base.entity.BaseEntity;
import com.example.ll.finalproject.app.keyword.entity.Keyword;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class HashTag extends BaseEntity {
    @ManyToOne
    @ToString.Exclude
    private Article article;

    @ManyToOne
    @ToString.Exclude
    private Keyword keyword;
}
