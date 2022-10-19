package com.example.ll.finalproject.product.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Setter
@Getter
public class ProductForm {
    @NotEmpty
    private String subject;
    @NotEmpty
    private String articleId;
    private int price;
    private String productTagContents;
}
