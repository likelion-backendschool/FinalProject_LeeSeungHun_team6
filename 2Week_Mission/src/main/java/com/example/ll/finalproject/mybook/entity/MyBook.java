package com.example.ll.finalproject.mybook.entity;

import com.example.ll.finalproject.base.entity.BaseEntity;
import com.example.ll.finalproject.member.entity.Member;
import com.example.ll.finalproject.product.entity.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class MyBook extends BaseEntity {
    @ManyToOne(fetch = LAZY)
    private Member member;
    @ManyToOne(fetch = LAZY)
    private Product product;
    
    private long number; //수량

    public MyBook(Member actor, Product product) {
        this.member = actor;
        this.product = product;
    }
}
