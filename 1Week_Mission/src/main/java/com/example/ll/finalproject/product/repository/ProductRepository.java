package com.example.ll.finalproject.product.repository;

import com.example.ll.finalproject.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
