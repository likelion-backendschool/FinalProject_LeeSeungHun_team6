package com.example.ll.finalproject.productTag.entity;

import com.example.ll.finalproject.base.entity.BaseEntity;
import com.example.ll.finalproject.keyword.entity.Keyword;
import com.example.ll.finalproject.product.entity.Product;
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
public class ProductTag extends BaseEntity {
    @ManyToOne
    @ToString.Exclude
    private Product product;

    @ManyToOne
    @ToString.Exclude
    private Keyword keyword;
}
