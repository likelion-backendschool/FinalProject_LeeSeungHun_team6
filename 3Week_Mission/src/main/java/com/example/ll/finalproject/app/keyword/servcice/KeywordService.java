package com.example.ll.finalproject.app.keyword.servcice;


import com.example.ll.finalproject.app.keyword.entity.Keyword;
import com.example.ll.finalproject.app.keyword.repositroy.KeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KeywordService {
    private final KeywordRepository keywordRepository;
    public Keyword save(String keywordContent) {

        Optional<Keyword> optKeyword = keywordRepository.findByContent(keywordContent);

        if(optKeyword.isPresent()){
            return optKeyword.get();
        }

        Keyword keyword = Keyword
                .builder()
                .content(keywordContent)
                .build();

        keywordRepository.save(keyword);
        return keyword;
    }

}
