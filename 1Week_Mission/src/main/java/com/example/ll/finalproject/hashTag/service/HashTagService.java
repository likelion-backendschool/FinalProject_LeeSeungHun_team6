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

    //해당 게시글에 해시태그를 붙이기 위한 작업
    public void applyHashTags(Article article, String hashTagContents) {
        List<HashTag> oldHashTags = getHashTags(article);

        //해시태그를 String형태의 List로 추출
        List<String> keywordContents = Arrays.stream(hashTagContents.split("#"))
                .map(String :: trim)
                .filter(s -> s.length() >0)
                .collect(Collectors.toList());


        List<HashTag> needToDelete = new ArrayList<>();

        //기존에 존재하던 해시태그를 삭제하는 작업
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

    public void deleteHashTag(List<HashTag> hashTags) {
        for(HashTag hashTag: hashTags){
            hashTagRepository.delete(hashTag);
        }
    }
}
