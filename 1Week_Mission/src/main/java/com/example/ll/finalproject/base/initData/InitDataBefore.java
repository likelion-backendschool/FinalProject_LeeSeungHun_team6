package com.example.ll.finalproject.base.initData;


import com.example.ll.finalproject.article.entity.Article;
import com.example.ll.finalproject.article.service.ArticleService;
import com.example.ll.finalproject.member.entity.Member;
import com.example.ll.finalproject.member.servie.MemberService;

public interface InitDataBefore {
    default void before(MemberService memberService, ArticleService articleService){
        String password = "1234";
        Member member1 = memberService.join("user1", password, "user1@test.com", "nickname1");
        Member member2 = memberService.join("user2", password, "user2@test.com", "nickname2");
        Member member3 = memberService.join("user3", password, "user3@test.com", "nickname3");
        Member member4 = memberService.join("user4", password, "user4@test.com", "nickname4");

        Article article1 = articleService.write(member1, "제목1", "내용1", "#자바 #프로그래밍");
        Article article2 = articleService.write(member1, "제목2", "내용2", "#HTML #프로그래밍");

    }
}
