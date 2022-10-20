package com.example.ll.finalproject.base.initData;

import com.example.ll.finalproject.article.service.ArticleService;
import com.example.ll.finalproject.keyword.servcice.KeywordService;
import com.example.ll.finalproject.member.servie.MemberService;
import com.example.ll.finalproject.product.service.ProductService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class DevInitData implements InitDataBefore{
    @Bean
    CommandLineRunner initData(MemberService memberService, ArticleService articleService, ProductService productService, KeywordService keywordService) {
        return args -> {
            before(memberService, articleService, productService, keywordService);
        };
    }
}
