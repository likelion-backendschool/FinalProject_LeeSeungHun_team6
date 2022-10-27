package com.example.ll.finalproject.product.repository;

import com.example.ll.finalproject.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByOrderByIdDesc();

    List<Product> findAllByAuthorId(long id);
}
