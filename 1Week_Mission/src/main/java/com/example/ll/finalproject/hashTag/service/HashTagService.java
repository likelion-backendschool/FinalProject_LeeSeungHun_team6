package com.example.ll.finalproject.hashTag.service;

import com.example.ll.finalproject.article.entity.Article;
import com.example.ll.finalproject.hashTag.entity.HashTag;
import com.example.ll.finalproject.hashTag.repository.HashTagRepository;
import com.example.ll.finalproject.keyword.entity.Keyword;
import com.example.ll.finalproject.keyword.servcice.KeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HashTagService {
    private final KeywordService keywordService;
    private final HashTagRepository hashTagRepository;
    public List<HashTag> getHashTagsByArticleIdIn(long[] ids) {
        return hashTagRepository.findAllByArticleIdIn(ids);
    }

    public void applyHashTags(Article article, String hashTagContents) {
        List<HashTag> oldHashTags = getHashTags(article);

        List<String> keywordContents = Arrays.stream(hashTagContents.split("#"))
                .map(String :: trim)
                .filter(s -> s.length() >0)
                .collect(Collectors.toList());


        List<HashTag> needToDelete = new ArrayList<>();

        for (HashTag oldHashTag : oldHashTags) {
            boolean contains = keywordContents.stream().anyMatch(s -> s.equals(oldHashTag.getKeyword().getContent()));

            if (contains == false) {
                needToDelete.add(oldHashTag);
            }
        }

        needToDelete.forEach(hashTag -> {
            hashTagRepository.delete(hashTag);
        });


        keywordContents.forEach(keywordContent -> {
            saveHashTag(article, keywordContent);
        });

    }
    private HashTag saveHashTag(Article article, String keywordContent) {
        Keyword keyword = keywordService.save(keywordContent);
        Optional<HashTag> opHashTag = hashTagRepository.findByArticleIdAndKeywordId(article.getId(), keyword.getId());
        if(opHashTag.isPresent()){
            return opHashTag.get();
        }
        HashTag hashTag = HashTag.builder()
                .article(article)
                .keyword(keyword)
                .build();

        hashTagRepository.save(hashTag);

        return hashTag;
    }
    public List<HashTag> getHashTags(Article article) {
        return hashTagRepository.findAllByArticleId(article.getId());
    }
    public List<HashTag> getHashTags(List<Article> articles) {
        //해당 멤버가 작성한 게시글들의 모든 키워드를 구하기 위함
        List<HashTag> hashTagFromArticles = new ArrayList<>();
        for(Article article:articles){
            List<HashTag> hashTags = hashTagRepository.findAllByArticleId(article.getId());
            //리스트에 리스트 추가
            hashTagFromArticles.addAll(hashTags);
            //중복 제거
            hashTagFromArticles = hashTagFromArticles.stream().distinct().collect(Collectors.toList());
        }
        return hashTagFromArticles;
    }


    public void deleteHashTag(List<HashTag> hashTags) {
        for(HashTag hashTag: hashTags){
            hashTagRepository.delete(hashTag);
        }
    }
}
