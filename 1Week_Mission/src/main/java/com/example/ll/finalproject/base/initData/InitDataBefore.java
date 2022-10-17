package com.example.ll.finalproject.base.initData;


import com.example.ll.finalproject.member.entity.Member;
import com.example.ll.finalproject.member.servie.MemberService;

public interface InitDataBefore {
    default void before(MemberService memberService){
        String password = "{noop}1234";
        Member member1 = memberService.join("user1", password, "user1@test.com");
        Member member2 = memberService.join("user2", password, "user2@test.com");
        Member member3 = memberService.join("user3", password, "user3@test.com");
        Member member4 = memberService.join("user4", password, "user4@test.com");
    }
}
