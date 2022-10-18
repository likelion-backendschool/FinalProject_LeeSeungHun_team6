package com.example.ll.finalproject.article.service;

import com.example.ll.finalproject.article.entity.Article;
import com.example.ll.finalproject.article.repository.ArticleRepository;
import com.example.ll.finalproject.hashTag.entity.HashTag;
import com.example.ll.finalproject.hashTag.service.HashTagService;
import com.example.ll.finalproject.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final HashTagService hashTagService;

    public List<Article> getArticles() {
        return articleRepository.findAllByOrderByIdDesc();
    }

    public void loadForPrintData(List<Article> articles) {
        long[] ids = articles
                .stream()
                .mapToLong(Article::getId)
                .toArray();

        List<HashTag> hashTagsByArticleIds = hashTagService.getHashTagsByArticleIdIn(ids);

        Map<Long, List<HashTag>> hashTagsByArticleIdsMap = hashTagsByArticleIds.stream()
                .collect(groupingBy(
                        hashTag -> hashTag.getArticle().getId(), toList()
                ));

        articles.stream().forEach(article -> {
            List<HashTag> hashTags = hashTagsByArticleIdsMap.get(article.getId());

            if (hashTags == null || hashTags.size() == 0) return;

            article.getExtra().put("hashTags", hashTags);
        });
    }

    public Article write(Member member, String subject, String content,String hashTagContents) {
        Article article = Article
                .builder()
                .author(member)
                .subject(subject)
                .content(content)
                .build();

        articleRepository.save(article);

        hashTagService.applyHashTags(article, hashTagContents);

        return article;
    }

    public Article getArticleById(Long id) {
        return articleRepository.findById(id).orElse(null);
    }

    public List<Article> search(String kwType, String kw) {
        return articleRepository.searchQsl(kwType, kw);
    }
}
