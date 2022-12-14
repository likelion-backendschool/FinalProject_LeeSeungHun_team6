package com.example.ll.finalproject.app.product.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Setter
@Getter
public class ProductForm {
    @NotEmpty
    private String subject;
    private int price;
    private String productTagContents;
    private String articleId;
}
