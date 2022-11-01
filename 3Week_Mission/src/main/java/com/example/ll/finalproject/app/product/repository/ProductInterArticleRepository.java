package com.example.ll.finalproject.app.product.repository;

import com.example.ll.finalproject.app.product.entity.ProductInterArticle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductInterArticleRepository extends JpaRepository<ProductInterArticle, Long> {

    List<ProductInterArticle> findAllByProductId(Long id);
}
