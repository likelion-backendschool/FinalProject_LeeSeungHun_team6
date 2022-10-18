package com.example.ll.finalproject.article.repository;

import com.example.ll.finalproject.article.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long>, ArticleRepositoryCustom{

    List<Article> findAllByOrderByIdDesc();
    List<Article> findTop100ByOrderByModifyDateDesc();
}
