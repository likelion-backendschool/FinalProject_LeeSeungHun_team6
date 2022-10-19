package com.example.ll.finalproject.product.dto.request;

import com.example.ll.finalproject.article.entity.Article;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class ProductForm {
    @NotEmpty
    private String subject;
    private int price;
    private String productTagContents;
    private String articleId;
}
