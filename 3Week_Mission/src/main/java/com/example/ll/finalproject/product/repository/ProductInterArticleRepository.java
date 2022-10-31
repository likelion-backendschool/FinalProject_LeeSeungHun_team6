package com.example.ll.finalproject.product.repository;

import com.example.ll.finalproject.article.entity.Article;
import com.example.ll.finalproject.product.entity.Product;
import com.example.ll.finalproject.product.entity.ProductInterArticle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductInterArticleRepository extends JpaRepository<ProductInterArticle, Long> {

    List<ProductInterArticle> findAllByProductId(Long id);
}
