package com.example.ll.finalproject.product.entity;

import com.example.ll.finalproject.base.entity.BaseEntity;
import com.example.ll.finalproject.keyword.entity.Keyword;
import com.example.ll.finalproject.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = PROTECTED)
public class Product extends BaseEntity {
    @ManyToOne(fetch = LAZY)
    private Member author;

    @ManyToOne(fetch = LAZY)
    private Keyword keyword;

    private String subject;
    private int price;
}
