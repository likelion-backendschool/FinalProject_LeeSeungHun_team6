package com.example.ll.finalproject.app.article.repository;

import com.example.ll.finalproject.app.article.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long>, ArticleRepositoryCustom{

    List<Article> findAllByOrderByIdDesc();

    //수정된 날짜가 가장 최근인 게시글 100개
    List<Article> findTop100ByOrderByModifyDateDesc();
    List<Article> findAllByAuthorId(Long id);
}
