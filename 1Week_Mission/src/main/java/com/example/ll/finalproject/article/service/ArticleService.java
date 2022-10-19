package com.example.ll.finalproject.article.service;

import com.example.ll.finalproject.article.entity.Article;
import com.example.ll.finalproject.article.repository.ArticleRepository;
import com.example.ll.finalproject.hashTag.entity.HashTag;
import com.example.ll.finalproject.hashTag.service.HashTagService;
import com.example.ll.finalproject.member.entity.Member;
import com.example.ll.finalproject.security.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public void loadForPrintData(Article article) {
        List<HashTag> hashTags = hashTagService.getHashTags(article);
        article.getExtra().put("hashTags", hashTags);
    }

    public Article write(Member member, String subject, String content,String contentHtml,String hashTagContents) {
        Article article = Article
                .builder()
                .author(member)
                .subject(subject)
                .content(content)
                .contentHtml(contentHtml)
                .build();

        articleRepository.save(article);

        hashTagService.applyHashTags(article, hashTagContents);

        return article;
    }

    public Article getArticleById(Long id) {
        return articleRepository.findById(id).orElse(null);
    }
    
    //View에서 받아온 ArticleID들의 값을 분리하고 그에 해당하는 ArticleList를 리턴
    public List<Article> getArticleById(String id) {
        List<Article> articles = new ArrayList<>();
        String[] articleIds = id.split(",");
        for(int i=0; i<articleIds.length; i++){
            Article article = this.getArticleById(Long.parseLong(articleIds[i]));
            articles.add(article);
        }
        return articles;
    }

    public Article getForPrintArticleById(Long id) {
        Article article = getArticleById(id);

        loadForPrintData(article);

        return article;
    }


    public void deleteArticle(Long id) {
        Article article = articleRepository.findById(id).orElse(null);
        List<HashTag> hashTags = hashTagService.getHashTags(article);
        hashTagService.deleteHashTag(hashTags);
        articleRepository.delete(article);
    }

    public void modify(Article article, String subject, String content,String contentHtml, String hashTagContents) {
        article.setSubject(subject);
        article.setContent(content);
        article.setContentHtml(contentHtml);

        articleRepository.save(article);
        hashTagService.applyHashTags(article, hashTagContents);
    }

    public List<Article> getRecentArticles() {
        return articleRepository.findTop100ByOrderByModifyDateDesc();
    }

    public List<Article> search(String kwType, String kw, MemberContext memberContext) {
        List<Article> articles = articleRepository.searchQsl(kwType, kw);
        for(int i=articles.size()-1; i>=0; i--){
            Article article = articles.get(i);
            String user1 = article.getAuthor().getUsername();
            String user2 = memberContext.getMember().getUsername();
            if(!user1.equals(user2)){
                articles.remove(article);
            }
        }
        if(articles==null){
            return null;
        }
        return articles;
    }

    public String getContentFromContentHtml(String articleFormContentHtml) {
        return articleFormContentHtml.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
    }

    public List<Article> findAllByMemberId(Long id) {
        return articleRepository.findAllByAuthorId(id);
    }


}
