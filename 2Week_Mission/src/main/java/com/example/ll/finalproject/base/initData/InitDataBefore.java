package com.example.ll.finalproject.base.initData;


import com.example.ll.finalproject.article.entity.Article;
import com.example.ll.finalproject.article.service.ArticleService;
import com.example.ll.finalproject.keyword.entity.Keyword;
import com.example.ll.finalproject.keyword.servcice.KeywordService;
import com.example.ll.finalproject.member.entity.Member;
import com.example.ll.finalproject.member.servie.MemberService;
import com.example.ll.finalproject.product.entity.Product;
import com.example.ll.finalproject.product.service.ProductService;

import java.util.ArrayList;
import java.util.List;

public interface InitDataBefore {
    default void before(MemberService memberService, ArticleService articleService, ProductService productService, KeywordService keywordService){
        String password = "1234";
        Member member1 = memberService.join("user1", password, "user1@test.com", "nickname1");
        Member member2 = memberService.join("user2", password, "user2@test.com", "nickname2");
        Member member3 = memberService.join("user3", password, "user3@test.com", "nickname3");
        Member member4 = memberService.join("user4", password, "user4@test.com", "nickname4");

        Article article1 = articleService.write(member1, "제목1", "내용1", "<p>내용1</p>","#자바 #프로그래밍");
        Article article2 = articleService.write(member2, "제목2", "내용2", "<p>내용2</p>","#HTML #프로그래밍");
        Article article3 = articleService.write(member3, "제목3", "내용3", "<p>내용3</p>","#자바 #프로그래밍");
        Article article4 = articleService.write(member4, "제목4", "내용4", "<p>내용4</p>","#HTML #네트워크");
        Article article5 = articleService.write(member1, "제목5", "내용5", "<p>내용5</p>","#자바 #DB");
        Article article6 = articleService.write(member2, "제목6", "내용6", "<p>내용6</p>","#HTML #프로그래밍");

//        Keyword keyword1 = keywordService.save("키워드1");
//        Keyword keyword2 = keywordService.save("키워드2");
        List<Article> articleList = new ArrayList<>();
        articleList.add(article1);
        articleList.add(article2);

        Product product1 = productService.create(member1, "제목1", 2_000, "#자바1 #프로그래밍1", articleList);
        Product product2 = productService.create(member1, "제목2", 4_000, "#자바2 #프로그래밍2", articleList);
        Product product3 = productService.create(member1, "제목3", 5_000, "#자바3 #프로그래밍3", articleList);
        Product product4 = productService.create(member1, "제목4", 6_000, "#자바4 #프로그래밍4", articleList);
        Product product5 = productService.create(member1, "제목5", 7_000, "#자바5 #프로그래밍5", articleList);



    }
}
