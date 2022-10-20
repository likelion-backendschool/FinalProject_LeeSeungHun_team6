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
    //상품에 포함되는 글을 디비에 저장
    public void saveArticles(Product product, List<Article> articles) {
          for(Article article : articles){
              ProductInterArticle productInterArticle = ProductInterArticle.builder()
                      .article(article)
                      .product(product)
                      .build();
              productInterArticleRepository.save(productInterArticle);
          }

    }

    //상품에 ID를 입력받으면 그에 해당하는 글 리스트를 출력
    public List<Article> getProductInterArticle(Long id) {
        List<ProductInterArticle> productInterArticles = productInterArticleRepository.findAllByProductId(id);
        List<Article> articles = new ArrayList<>();
        for(ProductInterArticle productInterArticle: productInterArticles){
            articles.add(productInterArticle.getArticle());
        }
        return articles;
    }

    
}
