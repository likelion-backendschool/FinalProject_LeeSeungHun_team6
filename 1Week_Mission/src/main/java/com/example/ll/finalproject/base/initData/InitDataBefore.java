package com.example.ll.finalproject.base.initData;


import com.example.ll.finalproject.article.entity.Article;
import com.example.ll.finalproject.article.service.ArticleService;
import com.example.ll.finalproject.keyword.entity.Keyword;
import com.example.ll.finalproject.keyword.servcice.KeywordService;
import com.example.ll.finalproject.member.entity.Member;
import com.example.ll.finalproject.member.servie.MemberService;
import com.example.ll.finalproject.product.entity.Product;
import com.example.ll.finalproject.product.service.ProductService;

public interface InitDataBefore {
    default void before(MemberService memberService, ArticleService articleService, ProductService productService, KeywordService keywordService){
        String password = "1234";
        Member member1 = memberService.join("user1", password, "user1@test.com", "nickname1");
        Member member2 = memberService.join("user2", password, "user2@test.com", "nickname2");
        Member member3 = memberService.join("user3", password, "user3@test.com", "nickname3");
        Member member4 = memberService.join("user4", password, "user4@test.com", "nickname4");

        Article article1 = articleService.write(member1, "제목1", "내용1", "<p>내용1</p>","#자바 #프로그래밍");
        Article article2 = articleService.write(member1, "제목2", "내용2", "<p>내용1</p>","#HTML #프로그래밍");

        Keyword keyword1 = keywordService.save("키워드1");
        Keyword keyword2 = keywordService.save("키워드2");


        Product product1 = productService.create(member1, "제목1", 2_000, keyword1);
        Product product2 = productService.create(member1, "제목2", 4_000, keyword2);


    }
}
