package com.example.ll.finalproject.product.service;

import com.example.ll.finalproject.article.entity.Article;
import com.example.ll.finalproject.product.entity.Product;
import com.example.ll.finalproject.product.entity.ProductInterArticle;
import com.example.ll.finalproject.product.repository.ProductInterArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductInterArticleService {
    private final ProductInterArticleRepository productInterArticleRepository;
    public void applyArticles(Product product, List<Article> articles) {
          for(Article article : articles){
              ProductInterArticle productInterArticle = ProductInterArticle.builder()
                      .article(article)
                      .product(product)
                      .build();
              productInterArticleRepository.save(productInterArticle);
          }

    }

    public List<Article> getProductInterArticle(Long id) {
        List<ProductInterArticle> productInterArticles = productInterArticleRepository.findAllByProductId(id);
        List<Article> articles = new ArrayList<>();
        for(ProductInterArticle productInterArticle: productInterArticles){
            articles.add(productInterArticle.getArticle());
        }
        return articles;
    }
}
