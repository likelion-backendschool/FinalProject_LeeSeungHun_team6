package com.example.ll.finalproject.product.service;

import com.example.ll.finalproject.article.entity.Article;
import com.example.ll.finalproject.cart.service.CartService;
import com.example.ll.finalproject.hashTag.entity.HashTag;
import com.example.ll.finalproject.member.entity.Member;
import com.example.ll.finalproject.product.entity.Product;
import com.example.ll.finalproject.product.repository.ProductRepository;
import com.example.ll.finalproject.productTag.entity.ProductTag;
import com.example.ll.finalproject.productTag.service.ProductTagService;
import com.example.ll.finalproject.security.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductTagService productTagService;
    private final CartService cartService;
    private final ProductInterArticleService productInterArticleService;

    //상품을 생성할 때 태그와 글을 같이 저장
    public Product create(Member member, String subject, int price, String productTagsContents, List<Article> articles) {
        Product product = Product.builder()
                .price(price)
                .author(member)
                .subject(subject)
                .build();

        productRepository.save(product);
        productTagService.applyProductTags(product, productTagsContents);
        productInterArticleService.saveArticles(product, articles);
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

    public void deleteProduct(Long id) {
        Product product = this.findById(id);
        List<ProductTag> productTags = productTagService.getProductTags(product);
        productTagService.deleteProductTag(productTags);
        productRepository.delete(product);
    }
    public void loadForPrintProduct(Product product) {
        List<ProductTag> productTags = productTagService.getProductTags(product);
        product.getExtra().put("productTags", productTags);
    }
    public Product getLoadForPrintProduct(long id) {
        Product product = productRepository.findById(id).orElse(null);
        loadForPrintProduct(product);
        return product;
    }

    public List<Product> findAllByMemberId(long id) {
        return productRepository.findAllByAuthorId(id);
    }
}
