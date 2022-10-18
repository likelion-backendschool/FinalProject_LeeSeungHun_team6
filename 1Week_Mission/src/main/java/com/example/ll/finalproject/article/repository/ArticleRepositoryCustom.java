package com.example.ll.finalproject.article.repository;

import com.example.ll.finalproject.article.entity.Article;

import java.util.List;

public interface ArticleRepositoryCustom {
    List<Article> getQslArticlesOrderByIdDesc();

    List<Article> searchQsl(String kwType, String kw);
}
