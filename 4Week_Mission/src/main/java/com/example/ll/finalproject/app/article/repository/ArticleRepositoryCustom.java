package com.example.ll.finalproject.app.article.repository;

import com.example.ll.finalproject.app.article.entity.Article;

import java.util.List;

public interface ArticleRepositoryCustom {
    List<Article> getQslArticlesOrderByIdDesc();

    List<Article> searchQsl(String kwType, String kw);
}
