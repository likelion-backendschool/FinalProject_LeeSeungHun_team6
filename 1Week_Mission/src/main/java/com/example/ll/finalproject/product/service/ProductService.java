package com.example.ll.finalproject.product.service;

import com.example.ll.finalproject.keyword.entity.Keyword;
import com.example.ll.finalproject.member.entity.Member;
import com.example.ll.finalproject.product.entity.Product;
import com.example.ll.finalproject.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public Product create(Member member, String subject, int price, Keyword keyword) {
        Product product = Product.builder()
                .price(price)
                .author(member)
                .keyword(keyword)
                .subject(subject)
                .build();

        productRepository.save(product);
        return product;
    }

    public Product findById(long id) {
        Product product = productRepository.findById(id).orElse(null);
        return product;
    }

    public void modify(Product product, String subject, int price) {
        product.setSubject(subject);
        product.setPrice(price);
        productRepository.save(product);
    }

    public List<Product> getProducts() {
        return productRepository.findAllByOrderByIdDesc();
    }
}
