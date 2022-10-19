package com.example.ll.finalproject.product.entity;

import com.example.ll.finalproject.article.entity.Article;
import com.example.ll.finalproject.base.entity.BaseEntity;
import com.example.ll.finalproject.keyword.entity.Keyword;
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
public class ProductInterArticle extends BaseEntity {
    @ManyToOne
    @ToString.Exclude
    private Article article;

    @ManyToOne
    @ToString.Exclude
    private Product product;
}
