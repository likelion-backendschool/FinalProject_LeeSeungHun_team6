package com.example.ll.finalproject.article.repository;

import com.example.ll.finalproject.article.entity.Article;
import com.example.ll.finalproject.util.Ut;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.example.ll.finalproject.article.entity.QArticle.article;
import static com.example.ll.finalproject.hashTag.entity.QHashTag.hashTag;
import static com.example.ll.finalproject.keyword.entity.QKeyword.keyword;


@RequiredArgsConstructor
public class ArticleRepositoryImpl implements ArticleRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;
    @Override
    public List<Article> getQslArticlesOrderByIdDesc() {
        return jpaQueryFactory
                .select(article)
                .from(article)
                .orderBy(article.id.desc())
                .fetch();
    }
    //해당되는 키워드를 가진 모든 글을 얻기 위함
    @Override
    public List<Article> searchQsl(String kwType, String kw) {
        JPAQuery<Article> jpqQuery = jpaQueryFactory
                .select(article)
                .distinct()
                .from(article);
        // 키워드가 존재하고
        if (Ut.str.empty(kw) == false) {
            // 키워드 타입이 해시태그이다.
            if (Ut.str.eq(kwType, "hashTag")) {
                jpqQuery
                        .innerJoin(hashTag)
                        .on(article.eq(hashTag.article))
                        .innerJoin(keyword)
                        .on(keyword.eq(hashTag.keyword))
                        .where(keyword.content.eq(kw));

            }
        }

        jpqQuery.orderBy(article.id.desc());

        return jpqQuery.fetch();
    }
}
