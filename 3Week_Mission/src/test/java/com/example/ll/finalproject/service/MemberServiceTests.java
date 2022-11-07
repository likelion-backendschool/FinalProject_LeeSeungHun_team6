package com.example.ll.finalproject.service;

import com.example.ll.finalproject.app.member.entity.Member;
import com.example.ll.finalproject.app.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class MemberServiceTests {
    @Autowired
    private MemberService memberService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원가입")
    void t1() {
        String username = "user10";
        String password = "1234";
        String email = "user10@test.com";
        String nickname="nickname1";

        memberService.join(username, password, email, nickname);

        Member foundMember = memberService.findByUsername("user10").get();
        assertThat(foundMember.getUsername()).isNotNull();
        assertThat(passwordEncoder.matches(password, foundMember.getPassword())).isTrue();
    }

    @Test
    @DisplayName("회원정보수정")
    void t2() {
        String username = "user10";
        String password = "1234";
        String email = "user10@test.com";
        String nickname="nickname10";

        memberService.join(username, password, email, nickname);

        Member member = memberService.findByUsername("user10").get();

        email = "modifyUser10@test.com";
        nickname="modifyNickname10";
        memberService.modify(member, email, nickname);

        assertThat(member.getNickname()).isEqualTo(nickname);
        assertThat(member.getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("ID 찾기")
    void t3() {
        String username = "user10";
        String password = "1234";
        String email = "user10@test.com";
        String nickname="nickname10";

        memberService.join(username, password, email, nickname);

        Member member = memberService.findByUsername("user10").get();
        String emailMock = "user11@test.com";
        String memberUsername = memberService.enrolledEmail(email);
        assertThat(memberUsername).isNotNull();
        String notMemberUsername = memberService.enrolledEmail(emailMock);
        assertThat(notMemberUsername).isNull();
    }



}
